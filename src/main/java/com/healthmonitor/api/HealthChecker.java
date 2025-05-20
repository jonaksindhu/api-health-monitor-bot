package com.healthmonitor.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.healthmonitor.api.model.Endpoint;
import com.healthmonitor.api.model.HealthCheckResult;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HealthChecker {

    private final ObjectMapper objectMapper;
    private final String configPath;

    public HealthChecker(String configPath) {
        this.objectMapper = new ObjectMapper();
        this.configPath = configPath;
    }

    public List<HealthCheckResult> runHealthChecks() {
        List<HealthCheckResult> results = new ArrayList<>();
        try {
            List<Endpoint> endpoints = loadEndpoints();

            for (Endpoint endpoint : endpoints) {
                HealthCheckResult result = checkEndpoint(endpoint);
                results.add(result);
                logResult(result);
            }

            saveResults(results);
        } catch (Exception e) {
            log.error("Error running health checks", e);
        }
        return results;
    }

    private List<Endpoint> loadEndpoints() throws IOException {
        File configFile = new File(configPath);
        Map<String, List<Map<String, String>>> config = objectMapper.readValue(configFile, 
            new TypeReference<Map<String, List<Map<String, String>>>>() {});
        
        List<Endpoint> endpoints = new ArrayList<>();
        for (Map<String, String> endpointMap : config.get("endpoints")) {
            Endpoint endpoint = new Endpoint();
            endpoint.setUrl(endpointMap.get("url"));
            endpoint.setMethod(endpointMap.get("method"));
            endpoint.setSchema(endpointMap.get("schema"));
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    private HealthCheckResult checkEndpoint(Endpoint endpoint) {
        HealthCheckResult result = new HealthCheckResult();
        result.setUrl(endpoint.getUrl());
        result.setTimestamp(LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        try {
            Response response = RestAssured.given()
                    .when()
                    .get(endpoint.getUrl());
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();
            boolean schemaValid = validateSchema(responseBody, endpoint.getSchema());
            result = new HealthCheckResult(endpoint.getUrl(), statusCode, responseTime, schemaValid, null);
            logResult(result);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            HealthCheckResult errorResult = new HealthCheckResult(endpoint.getUrl(), 0, responseTime, false, e.getMessage());
            logResult(errorResult);
            return errorResult;
        }
    }

    private boolean validateSchema(String responseBody, String schemaPath) {
        try {
            // Load schema as string
            File schemaFile = new File("schemas", schemaPath);
            String schemaContent = new String(java.nio.file.Files.readAllBytes(schemaFile.toPath()));
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaContent);

            // Parse response
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Validate
            java.util.Set<ValidationMessage> errors = schema.validate(jsonNode);
            return errors.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void logResult(HealthCheckResult result) {
        log.info("\nChecking {}:", result.getUrl());
        log.info("Status: {}", result.getStatusCode());
        log.info("Response Time: {}ms", result.getResponseTime());
        log.info("Schema Valid: {}", result.isSchemaValid());
        if (result.getError() != null) {
            log.info("Error: {}", result.getError());
        }
    }

    private void saveResults(List<HealthCheckResult> results) throws IOException {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE);
        java.nio.file.Path logDir = java.nio.file.Paths.get("logs");
        if (!java.nio.file.Files.exists(logDir)) {
            java.nio.file.Files.createDirectories(logDir);
        }

        java.nio.file.Path logFile = logDir.resolve("health-check-" + date + ".json");
        objectMapper.writeValue(logFile.toFile(), results);
    }
} 