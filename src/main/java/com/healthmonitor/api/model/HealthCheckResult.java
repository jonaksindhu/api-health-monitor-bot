package com.healthmonitor.api.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthCheckResult {
    private String url;
    private int statusCode;
    private long responseTime;
    private boolean schemaValid;
    private LocalDateTime timestamp;
    private String error;

    public HealthCheckResult(String url, int statusCode, long responseTime, boolean schemaValid, String error) {
        this.url = url;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.schemaValid = schemaValid;
        this.error = error;
    }

    public HealthCheckResult() {
        // Default constructor
    }
} 