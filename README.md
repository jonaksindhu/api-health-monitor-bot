# API Health Monitor

A Spring Boot application that monitors the health of API endpoints by checking their status codes, response times, and validating responses against JSON schemas.

## Features

- Periodically checks API endpoints from a configuration file
- Validates HTTP status codes (expects 2xx)
- Validates JSON responses against predefined schemas
- Measures and logs response times
- Generates daily JSON log reports
- Configurable check intervals

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
api-health-monitor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── healthmonitor/
│   │   │           └── api/
│   │   │               ├── model/
│   │   │               └── service/
│   │   └── resources/
│   └── test/
├── schemas/
├── logs/
├── urls.json
└── pom.xml
```

## Configuration

1. Configure your endpoints in `urls.json`:
```json
{
  "endpoints": [
    {
      "url": "https://api.example.com/endpoint",
      "method": "GET",
      "schema": "schema.json"
    }
  ]
}
```

2. Add JSON schemas in the `schemas/` directory

3. Configure application properties in `src/main/resources/application.properties`:
```properties
health.monitor.interval=300000  # Check interval in milliseconds
health.monitor.config-path=urls.json
```

## Building and Running

1. Build the application:
```bash
mvn clean package
```

2. Run the application:
```bash
java -jar target/api-health-monitor-1.0.0.jar
```

## Logs

The application generates daily log files in the `logs/` directory with the format `health-check-YYYY-MM-DD.json`.

## License

This project is licensed under the MIT License.