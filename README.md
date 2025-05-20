# API Health Monitor

A Java/Maven tool to monitor API endpoints for health, response time, and JSON schema validity.

## Quick Start

1. **Build:**
   ```bash
   mvn clean package
   ```
2. **Run all health checks and generate HTML report:**
   ```bash
   mvn test
   # See reports/health-check-report.html
   ```

## Configuration
- Define endpoints in `urls.json`:
  ```json
  {
    "endpoints": [
      { "url": "https://api.example.com/endpoint", "method": "GET", "schema": "schema.json" }
    ]
  }
  ```
- Place JSON schemas in the `schemas/` folder.

## Test Cases
The project includes comprehensive test cases that verify the health monitoring functionality:

1. **CoinDesk API Test**
   - Verifies API endpoint availability
   - Checks for expected error handling
   - Validates response time measurements
   - Ensures proper error reporting

2. **GitHub API Test**
   - Validates successful API responses (status code 200)
   - Verifies JSON schema validation
   - Checks response time performance (< 5 seconds)
   - Ensures error-free operation

3. **Open-Meteo API Test**
   - Confirms successful API responses (status code 200)
   - Validates JSON schema compliance
   - Monitors response time performance (< 5 seconds)
   - Verifies error-free operation

Each test generates detailed reports in the `reports/health-check-report.html` file, including:
- Test execution status
- Response times
- Schema validation results
- Error messages (if any)
- Detailed logs of each verification step

## Output
- Daily logs: `logs/health-check-YYYY-MM-DD.json`
- HTML report: `reports/health-check-report.html`