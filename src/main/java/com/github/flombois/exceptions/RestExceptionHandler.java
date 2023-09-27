package com.github.flombois.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String CONSTRAINT_VALIDATION_ERROR = "A constraint violation has occurred";
    public static final String DATA_INTEGRITY_VALIDATION_ERROR = "A data integrity violation has occurred";


    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ViolationErrorResponse> handleConstraintValidationException(ConstraintViolationException ex, WebRequest request) {
        ViolationErrorResponse response = new ViolationErrorResponse(CONSTRAINT_VALIDATION_ERROR, ex.getConstraintViolations().stream().map(this::toErrorMessage).toList());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Convert a constraint violation into a human-readable error message
     * @param constraintViolation The violation
     * @return The corresponding error message
     */
    protected String toErrorMessage(ConstraintViolation<?> constraintViolation) {
        return String.format("Property %s %s", constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<ViolationErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        if(ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintViolationException) {
            errorMessage = resolveHibernateConstrainViolationException(constraintViolationException);
        }
        ViolationErrorResponse response = new ViolationErrorResponse(DATA_INTEGRITY_VALIDATION_ERROR, List.of(errorMessage));
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    protected String resolveHibernateConstrainViolationException(org.hibernate.exception.ConstraintViolationException ex) {
        final Throwable cause = ex.getCause();
        if(cause instanceof PSQLException) {
            return cause.getMessage();
        }
        return "Unknown error";
    }
}
