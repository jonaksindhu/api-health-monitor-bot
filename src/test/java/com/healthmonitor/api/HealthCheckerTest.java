package com.healthmonitor.api;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.healthmonitor.api.model.HealthCheckResult;
import org.testng.annotations.*;
import org.testng.Assert;

import java.util.List;

public class HealthCheckerTest {

    private ExtentReports extent;
    private ExtentTest test;
    private HealthChecker healthChecker;

    @BeforeClass
    public void setup() {
        // Initialize ExtentReports
        extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("reports/health-check-report.html");
        spark.config().setDocumentTitle("API Health Check Report");
        spark.config().setReportName("API Health Check Results");
        extent.attachReporter(spark);

        // Initialize HealthChecker
        healthChecker = new HealthChecker("urls.json");
    }

    @Test(description = "Test CoinDesk API Health Check")
    public void testCoinDeskAPI() {
        test = extent.createTest("CoinDesk API Health Check", "Verify CoinDesk API health check functionality");
        
        // Step 1: Run health check
        test.log(Status.INFO, "Running health check for CoinDesk API");
        List<HealthCheckResult> results = healthChecker.runHealthChecks();
        
        // Step 2: Find CoinDesk API result
        test.log(Status.INFO, "Finding CoinDesk API result");
        HealthCheckResult coinDeskResult = results.stream()
            .filter(result -> result.getUrl().contains("api.coindesk.com"))
            .findFirst()
            .orElse(null);
        
        Assert.assertNotNull(coinDeskResult, "CoinDesk API result should not be null");
        
        // Step 3: Verify status code
        test.log(Status.INFO, "Verifying status code");
        Assert.assertEquals(coinDeskResult.getStatusCode(), 0, "CoinDesk API should fail with status code 0");
        
        // Step 4: Verify schema validation
        test.log(Status.INFO, "Verifying schema validation");
        Assert.assertFalse(coinDeskResult.isSchemaValid(), "CoinDesk API schema validation should fail");
        
        // Step 5: Verify error message
        test.log(Status.INFO, "Verifying error message");
        Assert.assertNotNull(coinDeskResult.getError(), "CoinDesk API should have an error message");
        Assert.assertTrue(coinDeskResult.getError().contains("nodename nor servname provided"), 
            "Error message should indicate DNS resolution failure");
        
        // Step 6: Verify response time
        test.log(Status.INFO, "Verifying response time");
        Assert.assertTrue(coinDeskResult.getResponseTime() > 0, "Response time should be greater than 0");
    }

    @Test(description = "Test GitHub API Health Check")
    public void testGitHubAPI() {
        test = extent.createTest("GitHub API Health Check", "Verify GitHub API health check functionality");
        
        // Step 1: Run health check
        test.log(Status.INFO, "Running health check for GitHub API");
        List<HealthCheckResult> results = healthChecker.runHealthChecks();
        
        // Step 2: Find GitHub API result
        test.log(Status.INFO, "Finding GitHub API result");
        HealthCheckResult githubResult = results.stream()
            .filter(result -> result.getUrl().contains("api.github.com"))
            .findFirst()
            .orElse(null);
        
        Assert.assertNotNull(githubResult, "GitHub API result should not be null");
        
        // Step 3: Verify status code
        test.log(Status.INFO, "Verifying status code");
        Assert.assertEquals(githubResult.getStatusCode(), 200, "GitHub API should return status code 200");
        
        // Step 4: Verify schema validation
        test.log(Status.INFO, "Verifying schema validation");
        Assert.assertTrue(githubResult.isSchemaValid(), "GitHub API schema validation should pass");
        
        // Step 5: Verify no error message
        test.log(Status.INFO, "Verifying no error message");
        Assert.assertNull(githubResult.getError(), "GitHub API should not have an error message");
        
        // Step 6: Verify response time
        test.log(Status.INFO, "Verifying response time");
        Assert.assertTrue(githubResult.getResponseTime() > 0, "Response time should be greater than 0");
        Assert.assertTrue(githubResult.getResponseTime() < 5000, "Response time should be less than 5 seconds");
    }

    @Test(description = "Test Open-Meteo API Health Check")
    public void testOpenMeteoAPI() {
        test = extent.createTest("Open-Meteo API Health Check", "Verify Open-Meteo API health check functionality");
        
        // Step 1: Run health check
        test.log(Status.INFO, "Running health check for Open-Meteo API");
        List<HealthCheckResult> results = healthChecker.runHealthChecks();
        
        // Step 2: Find Open-Meteo API result
        test.log(Status.INFO, "Finding Open-Meteo API result");
        HealthCheckResult openMeteoResult = results.stream()
            .filter(result -> result.getUrl().contains("api.open-meteo.com"))
            .findFirst()
            .orElse(null);
        
        Assert.assertNotNull(openMeteoResult, "Open-Meteo API result should not be null");
        
        // Step 3: Verify status code
        test.log(Status.INFO, "Verifying status code");
        Assert.assertEquals(openMeteoResult.getStatusCode(), 200, "Open-Meteo API should return status code 200");
        
        // Step 4: Verify schema validation
        test.log(Status.INFO, "Verifying schema validation");
        Assert.assertTrue(openMeteoResult.isSchemaValid(), "Open-Meteo API schema validation should pass");
        
        // Step 5: Verify no error message
        test.log(Status.INFO, "Verifying no error message");
        Assert.assertNull(openMeteoResult.getError(), "Open-Meteo API should not have an error message");
        
        // Step 6: Verify response time
        test.log(Status.INFO, "Verifying response time");
        Assert.assertTrue(openMeteoResult.getResponseTime() > 0, "Response time should be greater than 0");
        Assert.assertTrue(openMeteoResult.getResponseTime() < 5000, "Response time should be less than 5 seconds");
    }

    @AfterClass
    public void tearDown() {
        extent.flush();
    }
} 