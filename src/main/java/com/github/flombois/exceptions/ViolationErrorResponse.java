package com.github.flombois.exceptions;

import java.util.Collections;
import java.util.List;

public record ViolationErrorResponse(String message, List<String> violations) {

    public ViolationErrorResponse(String message, List<String> violations) {
        this.message = message;
        this.violations = Collections.unmodifiableList(violations);
    }
}
