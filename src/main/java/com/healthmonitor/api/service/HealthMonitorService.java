package com.healthmonitor.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmonitor.api.model.Endpoint;
import com.healthmonitor.api.model.HealthCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HealthMonitorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String configPath;

    public HealthMonitorService(
            @Value("${health.monitor.config-path:urls.json}") String configPath) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.configPath = configPath;
    }

    @Scheduled(fixedRateString = "${health.monitor.interval:300000}") // Default: 5 minutes
    public void runHealthChecks() {
        try {
            List<HealthCheckResult> results = new ArrayList<>();
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
    }

    private List<Endpoint> loadEndpoints() throws IOException {
        File configFile = new File(configPath);
        Map<String, List<Endpoint>> config = objectMapper.readValue(configFile, Map.class);
        return config.get("endpoints");
    }

    private HealthCheckResult checkEndpoint(Endpoint endpoint) {
        HealthCheckResult result = new HealthCheckResult();
        result.setUrl(endpoint.getUrl());
        result.setTimestamp(LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint.getUrl(),
                    HttpMethod.valueOf(endpoint.getMethod()),
                    null,
                    String.class
            );

            result.setStatusCode(response.getStatusCode().value());
            result.setResponseTime(System.currentTimeMillis() - startTime);

            if (endpoint.getSchema() != null) {
                validateSchema(endpoint.getSchema(), response.getBody(), result);
            } else {
                result.setSchemaValid(true);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            result.setResponseTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private void validateSchema(String schemaPath, String responseBody, HealthCheckResult result) {
        try {
            String schemaContent = Files.readString(Paths.get("schemas", schemaPath));
            JSONObject schema = new JSONObject(schemaContent);
            JSONObject response = new JSONObject(responseBody);
            
            // Basic schema validation (you might want to use a more robust validation library)
            boolean isValid = true;
            for (String key : schema.getJSONObject("properties").keySet()) {
                if (schema.getJSONArray("required").toList().contains(key) && !response.has(key)) {
                    isValid = false;
                    break;
                }
            }
            result.setSchemaValid(isValid);
        } catch (Exception e) {
            result.setSchemaValid(false);
            result.setError("Schema validation error: " + e.getMessage());
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
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        Path logDir = Paths.get("logs");
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
        }

        Path logFile = logDir.resolve("health-check-" + date + ".json");
        objectMapper.writeValue(logFile.toFile(), results);
    }
} 