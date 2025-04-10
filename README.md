# Resona API Server(SNS)
> A scalable SNS API server built with Spring Boot and Oracle Cloud, supporting feeds, comments, and member management.

This server is divided into two main domains. Member and Social Media.

The Member domain is responsible for member registration and login, account status, and managing member profiles.

The Social Media domain handles feed uploads, comments, replies, and other interactions on the platform.

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
| Logging    | Log4j2, MongoDB             |

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

### 📦 Code Location

- [`ObjectStorageService.java`](src/main/java/synapps/resona/api/external/file/ObjectStorageService.java)
- [`ObjectStorageController.java`](src/main/java/synapps/resona/api/external/file/ObjectStorageController.java)


> For full implementation details, including error handling and metadata construction, please refer to the source code linked above.

---

## 🪵 Logging

This project uses **Log4j2** for logging. Logs are written both to a file and to a **MongoDB** collection.

We recommend using Log4j directly rather than wrapping it through `slf4j`, to take full advantage of Log4j2’s features.

### ✅ How to Use
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleClass {
    private final Logger logger = LogManager.getLogger(ExampleClass.class);

    private void loggingExample() {
        logger.info("Log example");
    }
}
```

### 🧾 Logging Destinations

- `Console`: for local development
- `RollingFile`: logs saved to `./logs/spring-boot-logger-log4j2.log` with daily and size-based rotation
- `MongoDB`: logs stored in a capped collection for efficient and searchable persistence

### ⚙️ Configuration

Log4j2 is configured in [`resources/log4j2-spring.xml`](./src/main/resources/log4j2-spring.xml).  
Here is a snippet of how MongoDB is integrated.

```xml
<NoSql name="MongoDbAppender">
    <MongoDb4 connection="mongodb://${sys:MONGO_USERNAME}:${sys:MONGO_PASSWORD}@${sys:MONGO_HOST}:${sys:MONGO_PORT}/${sys:MONGO_DB_NAME}?authSource=${sys:MONGO_AUTH_DATABASE}"
              capped="true"
              collectionSize="1073741824"/>
</NoSql>
```

- The MongoDB appender uses a **capped collection** of 1GB for efficient storage and automatic document eviction.
- Connection parameters are injected through system properties.

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

### 📦 Related Classes

- [`GlobalExceptionHandler`](src/main/java/synapps/resona/api/global/handler/GlobalExceptionHandler.java)
- [`CustomAuthenticationEntryPoint`](src/main/java/synapps/resona/api/global/handler/CustomAuthenticationEntryPoint.java)
- [`CustomAccessDeniedHandler`](src/main/java/synapps/resona/api/global/handler/CustomAccessDeniedHandler.java)
- [`ErrorMetaDataDto`](src/main/java/synapps/resona/api/global/dto/metadata/ErrorMetaDataDto.java)

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
