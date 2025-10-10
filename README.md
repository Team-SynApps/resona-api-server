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
- Docker
- Docker Compose

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
- [Event-Driven Architecture](#-event-driven-architecture)
- [API Documentation](#-api-documentation)
- [File Upload](#-file-upload)
- [Logging](#-logging)
- [Error Handling](#-error-handling)
- [Test](#test)
- [CI/CD](#-ci/cd)
- [License](#-license)
- [Author](#-author)

## 🛠️ Tech Stack

| Category   | Technology                  |
|------------|-----------------------------|
| Language   | Java 17                     |
| Framework  | Spring Boot 3.2.3           |
| Build Tool | Gradle                      |
| Database   | MySQL 8.0, MongoDB (Oracle ADB) |
| ORM        | Spring Data JPA (Hibernate), Spring Data MongoDB |
| Security   | Spring Security, JWT        |
| API Spec   | Swagger                     |
| DevOps     | Docker, Docker Compose, GitHub Actions |
| Cloud      | Oracle Cloud Infrastructure |
| Monitoring | Prometheus / Grafana        |
| Test       | JUnit 5, AssertJ            |
| Logging    | Logback                     |

## ⚙️ Configuration
The project's configuration is managed through a hierarchical system of YAML files and environment variables, powered by Spring Profiles and the `dotenv-gradle` plugin.

- **`application.yml`**: Contains common configuration properties shared across all environments.
- **`application-{profile}.yml`**: Holds environment-specific settings (e.g., `application-dev.yml` for development, `application-prod.yml` for production).
- **`.env`**: For sensitive data like database credentials or API keys. This file is loaded by the `dotenv-gradle` plugin and its values are exposed as environment variables. It should be included in `.gitignore` and not committed to the repository.

The priority is as follows: `.env` variables > profile-specific YAML > common YAML.

---

## 📢 Event-Driven Architecture
The Resona API Server utilizes an event-driven architecture based on Spring's Application Events for loose coupling and improved scalability. Key components publish events, and various listeners react to them to perform actions like sending notifications or updating read models.

Here are some examples of event flows in the system:

| Event                   | Publisher (Module)                               | Subscriber(s) (Module)                                                                                                                            | Description                                                                 |
|-------------------------|--------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `MemberRegisteredEvent` | `AuthService` (`member`)                         | `MemberNotificationEventListener` (`notification`)                                                                                                | When a new member signs up, a notification is sent.                         |
| `FeedCreatedEvent`      | `FeedCommandService` (`social-media`)            | `SpringEventFanoutListener` (`social-media`)<br>`SpringFeedEventListener` (`social-media`)                                                          | When a new feed is posted, it is fanned out to followers' timelines and the read model is updated for efficient querying. |

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
This project widely uses Lombok's **`@Slf4j`** annotation to reduce boilerplate code. This is the recommended approach.

By adding the `@Slf4j` annotation to a class, a `log` field is automatically created and initialized.

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleClass {

    private void loggingExample() {
        log.info("Log example with @Slf4j");
        log.error("This is an error message.");
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

## 🌐 CI/CD

The project utilizes GitHub Actions for Continuous Integration and Continuous Deployment. Workflows are configured to automate building, testing, and deploying the application.

- **`build_deploy.yml`**: Handles building the application, running tests, building Docker images, and deploying to Oracle Cloud Infrastructure.
- **`code_review.yml`**: Automates code review processes.

--- 

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