package com.healthmonitor.api.model;

import lombok.Data;

@Data
public class Endpoint {
    private String url;
    private String method;
    private String schema;
} 