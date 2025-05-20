package com.healthmonitor.api;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.healthmonitor.api.model.Endpoint;
import com.healthmonitor.api.model.HealthCheckResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HealthCheckerTest {

    private static ExtentReports extent;
    private static ExtentTest test;
    private static HealthChecker healthChecker;

    @BeforeAll
    public static void setup() {
        // Initialize ExtentReports
        extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("reports/health-check-report.html");
        spark.config().setDocumentTitle("API Health Check Report");
        spark.config().setReportName("API Health Check Results");
        extent.attachReporter(spark);

        // Initialize HealthChecker
        healthChecker = new HealthChecker("urls.json");
    }

    @Test
    public void testHealthChecks() {
        test = extent.createTest("API Health Checks", "Running health checks for configured endpoints");
        
        try {
            // Create reports directory if it doesn't exist
            new File("reports").mkdirs();

            // Run health checks
            List<HealthCheckResult> results = healthChecker.runHealthChecks();

            // Log results to the report
            for (HealthCheckResult result : results) {
                String status = result.getError() == null ? "PASS" : "FAIL";
                String details = String.format(
                    "URL: %s\nStatus Code: %d\nResponse Time: %dms\nSchema Valid: %s",
                    result.getUrl(),
                    result.getStatusCode(),
                    result.getResponseTime(),
                    result.isSchemaValid()
                );

                if (result.getError() != null) {
                    details += "\nError: " + result.getError();
                }

                if (status.equals("PASS")) {
                    test.log(Status.PASS, details);
                } else {
                    test.log(Status.FAIL, details);
                }
            }
        } catch (Exception e) {
            test.log(Status.FAIL, "Error running health checks: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        extent.flush();
    }
} 