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

## Output
- Daily logs: `logs/health-check-YYYY-MM-DD.json`
- HTML report: `reports/health-check-report.html`