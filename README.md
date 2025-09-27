# Resona API Server (SNS)

> A scalable, multi-module SNS API server built with Spring Boot, designed for high performance and maintainability.

Resona API Server is architected as a multi-module project to ensure clear separation of concerns, improved scalability, and easier maintenance. Each domain is developed and managed in its own module, promoting loose coupling and high cohesion.

## 🏗️ Project Structure

The project is divided into the following modules:

| Module         | Description                                                  |
|----------------|--------------------------------------------------------------|
| `application`  | The main entry point of the application. Handles global configurations, request routing, and application startup. |
| `core`         | Contains common code, utilities, entities, and core business logic shared across all modules. |
| `member`       | Manages all member-related functionalities, including authentication, profiles, and user accounts. |
| `social-media` | Handles core social media features like feeds, posts, comments, and user interactions. |
| `chat`         | Provides real-time chatting functionalities.                |
| `notification` | Manages and sends notifications to users.                    |

## 🚀 Getting Started

### Prerequisites
- Java 17
- Gradle

### Build
To build the entire project, run the following command from the root directory:
```shell
./gradlew clean build
```

### Run
To run the application, execute the `bootRun` task from the `application` module:
```shell
./gradlew :application:bootRun
```

## 📑 Table of Contents

- [Tech Stack](#-tech-stack)
- [API Documentation](#-api-documentation)
- [File Upload](#-file-upload)
- [Logging](#-logging)
- [Error Handling](#-error-handling)
- [Test](#test)
- [License](#-license)
- [Author](#-author)

## 🛠️ Tech Stack

| Category   | Technology                  |
|------------|-----------------------------|
| Language   | Java 17                     |
| Framework  | Spring Boot 3.2.3           |
| Build Tool | Gradle                      |
| Database   | MySQL 8.0                   |
| ORM        | Spring Data JPA (Hibernate) |
| Security   | Spring Security, JWT        |
| API Spec   | Swagger                     |
| DevOps     | Docker, GitHub Actions      |
| Cloud      | Oracle Cloud Infrastructure |
| Monitoring | Prometheus / Grafana        |
| Test       | JUnit 5, AssertJ            |
| Logging    | Logback                     |

---

## 📚 API Documentation

The project uses **Swagger** for documenting REST APIs.

> API docs (Swagger UI) are available in the production environment and accessible to authorized users only.
> For internal use during development and testing.

---

## 📁 File Upload

This project uses **OCI Object Storage (oci-bucket)** for file uploads, using **Oracle Java SDK v3.43.1**.

### ✅ Upload Flow

1. Upload file to **buffer bucket** (`PutObjectRequest`)
2. On confirmation, copy file to **disk bucket** (`CopyObjectRequest`)
3. Extract and return file metadata (name, size, content type, dimensions, etc.)

### 🔗 References

- [Java SDK](https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/javasdk.htm)
- [Put Object](https://docs.oracle.com/en-us/iaas/api/#/en/objectstorage/20160918/Object/PutObject)
- [Copy Object](https://docs.oracle.com/en-us/iaas/api/#/en/objectstorage/20160918/Object/CopyObject)

---

## 🪵 Logging

This project uses **Logback** for logging, configured via `logback-spring.xml`. The system is set up for structured logging, producing JSON-formatted logs for easier parsing and analysis.

We recommend using the **SLF4J API** for logging consistency.

### ✅ How to Use
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleClass {
    private final Logger logger = LoggerFactory.getLogger(ExampleClass.class);

    private void loggingExample() {
        logger.info("Log example");
    }
}
```

### 🧾 Logging Destinations

- `Console`: For real-time monitoring during local development.
- `JSON File`: Logs are written to a rolling file in JSON format. This is ideal for aggregation in log management systems.
  - **File Path**: `logs/resona-api.log`
  - **Rotation**: Daily, with a 30-day history.

### ⚙️ Configuration

Logback is configured in `application/src/main/resources/logback-spring.xml`.  
Here is a snippet of the file appender configuration, which uses `LogstashEncoder` to generate structured JSON logs.

```xml
<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_PATH}/${LOG_FILE_NAME}.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_PATH}/${LOG_FILE_NAME}.%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <customFields>{"application_name":"${APP_NAME}"}</customFields>
    </encoder>
</appender>
```

> For full details, see the complete XML configuration file.

---

## 🚨 Error Handling

This project uses a centralized error handling strategy based on `@RestControllerAdvice` and Spring Security hooks.
All exceptions are converted into a consistent JSON response with detailed metadata.

### ✅ Features
- Consistent error response structure with status, message, path, server info, and request ID
- Custom error codes for domain-specific exceptions
- Handles:
  - Business exceptions (`BaseException`)
  - Validation errors (`MethodArgumentNotValidException`)
  - Authentication errors (`AuthenticationEntryPoint`)
  - Authorization errors (`AccessDeniedHandler`)
  - Unexpected internal exceptions

### 💥 Example Error Response

```json
{
  "meta": {
    "status": 404,
    "message": "Feed not found",
    "timestamp": "2025-04-03 14:22:01",
    "path": "/api/feed/100",
    "apiVersion": "v1",
    "serverName": "resona-api",
    "requestId": "5a1f56b4-e2e9-490c-8c30-abcde1234567",
    "errorCode": "FEED001"
  },
  "data": [
    {
      "error": "Feed not found"
    }
  ]
}
```

> Error response metadata is built using `ErrorMetaDataDto`, and every exception includes a unique `requestId` to help trace issues.

---

## Test
This project follows a layered architecture and adopts a strategic approach to writing test cases.

### Essential Layers to Test
- **Service Layer**: Business logic is crucial, so it must be tested.
- **Repository Layer**: If complex queries exist, they should be tested.
- **Controller Layer**: If APIs are exposed, they should be tested.

### Optional Layers to Test
- **Entity Layer**: Test only if domain logic is included.
- **Basic CRUD Repository**: Spring Data JPA ensures basic CRUD operations, so explicit tests are optional.

Test cases are written based on these principles to ensure efficiency and maintainability.

## 📄 License

This software is licensed under a **custom Software License Agreement**.

- All rights reserved by **speculatingwook (윤병욱)**.
- The software is provided for **personal, non-commercial use only**.
- Redistribution, modification, and commercial use are strictly prohibited.

For full license details, see [`LICENSE`](./LICENSE).

## ✍️ Author

This project is developed and maintained by [speculatingwook (윤병욱)](https://github.com/speculatingwook).

- **GitHub**: [speculatingwook](https://github.com/speculatingwook)
- **Blog**: [Tech Blog(Written in Korean)](https://blog-full-of-desire-v3.vercel.app/)