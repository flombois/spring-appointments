package com.github.flombois.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface FetchResourceCollectionTest extends ResourceTest {

    int getDefaultPageSize();

    default ResultActions checkPagination(ResultActions resultActions) throws Exception {
        // Ensure pagination is properly set
        resultActions.andExpect(jsonPath("$.page.size").value(getDefaultPageSize()))
                .andExpect(jsonPath("$.page.totalElements").value(20))
                .andExpect(jsonPath("$.page.totalPages").value(20 / getDefaultPageSize()))
                .andExpect(jsonPath("$.page.number").value(0));
        return resultActions;
    }

}
