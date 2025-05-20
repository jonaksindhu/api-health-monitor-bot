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
} 