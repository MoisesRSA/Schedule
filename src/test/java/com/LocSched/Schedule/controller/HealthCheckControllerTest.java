package com.LocSched.Schedule.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class HealthCheckControllerTest {

    private MockMvc mockMvc;
    private HealthCheckController healthCheckController;
    private final String testSecret = "test-secret-key-123";

    @BeforeEach
    void setUp() {
        healthCheckController = new HealthCheckController();
        ReflectionTestUtils.setField(healthCheckController, "secretKey", testSecret);
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();
    }

    @Test
    void healthCheck_withValidQueryParamKey_returnsOk() throws Exception {
        mockMvc.perform(get("/api/public/health")
                .param("key", testSecret))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void healthCheck_withValidHeaderKey_returnsOk() throws Exception {
        mockMvc.perform(get("/api/public/health")
                .header("X-Health-Key", testSecret))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void healthCheck_withInvalidQueryParamKey_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/public/health")
                .param("key", "wrong-secret"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized access"));
    }

    @Test
    void healthCheck_withInvalidHeaderKey_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/public/health")
                .header("X-Health-Key", "wrong-secret"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized access"));
    }

    @Test
    void healthCheck_withNoKey_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/public/health"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized access"));
    }
}
