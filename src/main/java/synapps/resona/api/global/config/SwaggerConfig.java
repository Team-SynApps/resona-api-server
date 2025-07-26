package synapps.resona.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.dto.code.ErrorCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import synapps.resona.api.global.dto.code.SuccessCode;
import synapps.resona.api.mysql.token.AuthToken;

/**
 * Swagger (OpenAPI 3.0) 설정을 위한 클래스입니다.
 * 전역적인 API 정보, 보안 설정 및 커스텀 에러 응답 자동화를 처리합니다.
 */
@Configuration
public class SwaggerConfig {

  @Value("${swagger.server.url}")
  private String serverUrl;

  /**
   * OpenAPI의 기본 정보를 설정하는 Bean을 생성합니다.
   * API 문서의 제목, 버전, 설명, 보안 요구사항, 서버 URL 등을 정의합니다.
   * @return 기본 정보가 설정된 OpenAPI 객체
   */
  @Bean
  public OpenAPI openAPI() {
    Components components = new Components()
        .addSecuritySchemes("bearerAuth", getSecurityScheme())
        .addSchemas("CursorMeta", createCursorMetaSchema())
        .addSchemas("CursorSuccessResponse", createCursorSuccessResponseSchema())
        .addSchemas("SuccessMeta", createSuccessMetaSchema())
        .addSchemas("SuccessResponse", createSuccessResponseSchema())
        .addSchemas("ErrorMeta", createErrorMetaSchema())
        .addSchemas("ErrorResponse", createErrorResponseSchema());

    return new OpenAPI()
        .info(new Info()
            .title("Resona SNS Server API")
            .description("Provides SNS, Member APIs")
            .version("1.0.0"))
        .components(components)
        .security(List.of(getSecurityRequirement()))
        .servers(List.of(new Server().url(serverUrl)));
  }

  /**
   * 각 API Operation을 순회하며 커스텀 에러 응답을 동적으로 추가하는 OperationCustomizer를 생성합니다.
   * 컨트롤러 메서드에 {@link ApiErrorSpec} 어노테이션이 붙어있는 경우, 해당 정보를 바탕으로
   * 가능한 에러 응답들을 Swagger 문서에 자동으로 반영합니다.
   * @return 커스텀 로직이 적용된 OperationCustomizer 객체
   */
  @Bean
  public OperationCustomizer operationCustomizer() {
    return (operation, handlerMethod) -> {
      // 1. 에러 응답 처리
      ApiErrorSpec apiErrorSpec = handlerMethod.getMethodAnnotation(ApiErrorSpec.class);
      if (apiErrorSpec != null) {
        generateErrorResponseDocs(operation, apiErrorSpec.value());
      }
      // 2. 성공 응답 처리
      ApiSuccessResponse apiSuccessResponse = handlerMethod.getMethodAnnotation(ApiSuccessResponse.class);
      if (apiSuccessResponse != null) {
        generateSuccessResponseDoc(operation, apiSuccessResponse.value());
      }

      return operation;
    };
  }

  /**
   * ErrorCodeSpec 배열을 바탕으로 Operation에 에러 응답들을 추가합니다.
   * @param operation Swagger Operation 객체
   * @param errorSpecs 컨트롤러에 명시된 에러 스펙 배열
   */
  private void generateErrorResponseDocs(Operation operation, ErrorCodeSpec[] errorSpecs) {
    ApiResponses apiResponses = operation.getResponses();
    if (apiResponses == null) {
      apiResponses = new ApiResponses();
      operation.setResponses(apiResponses);
    }

    ApiResponses finalApiResponses = apiResponses;
    Stream.of(errorSpecs)
        .flatMap(this::getErrorCodeStream)
        .forEach(errorCode -> {
          ApiResponse apiResponse = createApiResponse(errorCode);
          finalApiResponses.addApiResponse(String.valueOf(errorCode.getStatus().value()), apiResponse);
        });
  }

  /**
   * SuccessCodeSpec 정보를 바탕으로 Operation의 성공 응답 설명을 업데이트합니다.
   * @param operation Swagger Operation 객체
   * @param successCodeSpec 컨트롤러에 명시된 SuccessCode 스펙
   */
  private void generateSuccessResponseDoc(Operation operation, SuccessCodeSpec successCodeSpec) {
    SuccessCode successCode = (SuccessCode) Enum.valueOf((Class) successCodeSpec.enumClass(), successCodeSpec.code());
    String statusCode = String.valueOf(successCode.getStatusCode());
    String successCodeName = successCodeSpec.code();

    // [수정] 재귀 호출을 위해 depth 파라미터 추가
    Object exampleData;
    if (successCodeSpec.listElementClass() != Void.class) {
      // 리스트 예시는 2개 정도 생성
      exampleData = List.of(
          createExampleData(successCodeSpec.listElementClass(), 0),
          createExampleData(successCodeSpec.listElementClass(), 0)
      );
    } else {
      // 기존 단일 객체 처리
      exampleData = createExampleData(successCodeSpec.responseClass(), 0);
    }

    // ... (이하 로직은 수정 없음)
    Map<String, Object> metaMap = new LinkedHashMap<>();
    metaMap.put("status", successCode.getStatusCode());
    metaMap.put("message", successCode.getMessage());
    metaMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    metaMap.put("path", "/example/path");
    metaMap.put("apiVersion", "v1");
    metaMap.put("serverName", "resona-api");
    metaMap.put("requestId", UUID.randomUUID().toString());

    if (successCodeSpec.cursor()) {
      metaMap.put("cursor", "string");
      metaMap.put("size", 10);
      metaMap.put("hasNext", true);
    }

    Map<String, Object> responseExampleMap = new LinkedHashMap<>();
    responseExampleMap.put("meta", metaMap);
    responseExampleMap.put("data", exampleData);

    Example example = new Example();
    example.setSummary(successCode.getMessage());
    example.setValue(responseExampleMap);

    String schemaRef = successCodeSpec.cursor() ? "#/components/schemas/CursorSuccessResponse" : "#/components/schemas/SuccessResponse";

    MediaType mediaType = new MediaType();
    mediaType.setSchema(new Schema<>().$ref(schemaRef));
    mediaType.addExamples(successCodeName, example);

    Content content = new Content().addMediaType("application/json", mediaType);

    ApiResponse newApiResponse = new ApiResponse()
        .description(successCode.getMessage())
        .content(content);

    operation.getResponses().addApiResponse(statusCode, newApiResponse);

    if (statusCode.equals("201")) {
      operation.getResponses().remove("200");
    }
  }

  /**
   * DTO 클래스를 기반으로 동적 예시 데이터를 생성하는 헬퍼 메서드
   * @param clazz 예시를 생성할 DTO 클래스
   * @return 생성된 예시 데이터 (Map 또는 null)
   */
  private Object createExampleData(Class<?> clazz, int depth) {
    // 1. 무한 재귀 방지
    if (depth > 5) return "Too deep recursion";

    // 2. 기본/단순 타입 처리 (재귀의 종료 지점)
    if (clazz == null || clazz == Void.class || clazz == void.class) return null;
    if (String.class.isAssignableFrom(clazz)) return "string";
    if (Integer.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) return 0;
    if (Number.class.isAssignableFrom(clazz)) return 0.0;
    if (Boolean.class.isAssignableFrom(clazz)) return true;
    if (LocalDate.class.isAssignableFrom(clazz)) return LocalDate.now().toString();
    if (LocalDateTime.class.isAssignableFrom(clazz)) return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    if (clazz.isEnum()) return clazz.getEnumConstants()[0].toString();
    if (Collection.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz)) return List.of("string_item_1", "string_item_2");

    // 3. AuthToken 같은 특수 타입 처리
    if (AuthToken.class.isAssignableFrom(clazz)) {
      Map<String, Object> tokenExample = new LinkedHashMap<>();
      tokenExample.put("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcyMTk4NDcxOCwiZXhwIjoxNzIyNTg5NTE4fQ.abc123def456");
      return tokenExample;
    }

    // 4. 프로젝트 내의 DTO 객체일 경우, 필드를 순회하며 재귀 호출
    // (주의: 패키지 경로는 실제 프로젝트에 맞게 확인해주세요)
    if (clazz.getPackageName().startsWith("synapps.resona.api")) {
      try {
        Map<String, Object> example = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
          field.setAccessible(true);
          // 재귀 호출로 하위 객체 예시 생성
          example.put(field.getName(), createExampleData(field.getType(), depth + 1));
        }
        return example;
      } catch (Exception e) {
        return "Failed to generate example for " + clazz.getSimpleName();
      }
    }

    // 5. 위 모든 경우에 해당하지 않으면, 클래스 이름 반환
    return clazz.getSimpleName() + " object";
  }

  /**
   * ErrorCodeSpec에 명시된 Enum 클래스와 코드 이름을 이용해 실제 ErrorCode 상수 스트림을 생성합니다.
   * @param errorSpec 단일 에러 스펙 정보
   * @return ErrorCode 상수 스트림
   */
  private Stream<ErrorCode> getErrorCodeStream(ErrorCodeSpec errorSpec) {
    Class<? extends ErrorCode> enumClass = errorSpec.enumClass();
    String[] codeNames = errorSpec.codes();
    return Stream.of(codeNames)
        .map(name -> {
          try {
            // 문자열로 된 코드 이름을 실제 Enum 상수로 변환
            return (ErrorCode) Enum.valueOf((Class) enumClass, name);
          } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ErrorCode name '" + name + "' in enum " + enumClass.getSimpleName(), e);
          }
        });
  }

  /**
   * ErrorCode 정보를 바탕으로 동적인 예시를 포함한 ApiResponse 객체를 생성합니다.
   * @param errorCode 에러 코드 정보
   * @return Swagger 문서에 추가될 ApiResponse 객체
   */
  private ApiResponse createApiResponse(ErrorCode errorCode) {
    // 실제 응답과 유사한 예시(Example)를 동적으로 생성
    Map<String, Object> metaMap = new LinkedHashMap<>();
    metaMap.put("status", errorCode.getStatusCode());
    metaMap.put("message", errorCode.getMessage());
    metaMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    metaMap.put("path", "/example/path");
    metaMap.put("apiVersion", "v1");
    metaMap.put("serverName", "resona-api");
    metaMap.put("requestId", UUID.randomUUID().toString());
    metaMap.put("customErrorCode", errorCode.getCustomCode());

    Map<String, Object> responseExampleMap = Map.of(
        "meta", metaMap,
        "data", "null"
    );

    Example example = new Example();
    example.setValue(responseExampleMap);

    return new ApiResponse()
        .description(errorCode.getMessage())
        .content(new Content().addMediaType(
            "application/json",
            new MediaType()
                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                .addExamples(errorCode.getCustomCode(), example)
        ));
  }

  // --- 아래는 스키마 및 보안 설정을 위한 private helper 메서드들 ---

  private Schema<?> createErrorMetaSchema() {
    return new Schema<>()
        .type("object")
        .name("ErrorMeta")
        .addProperty("status", new Schema<>().type("integer").description("HTTP 상태 코드"))
        .addProperty("message", new Schema<>().type("string").description("응답 메시지"))
        .addProperty("timestamp", new Schema<>().type("string").description("응답 타임스탬프"))
        .addProperty("path", new Schema<>().type("string").description("요청 경로"))
        .addProperty("apiVersion", new Schema<>().type("string").description("API 버전"))
        .addProperty("serverName", new Schema<>().type("string").description("서버 이름"))
        .addProperty("requestId", new Schema<>().type("string").description("요청 ID (UUID)"))
        .addProperty("customErrorCode", new Schema<>().type("string").description("커스텀 에러 코드"));
  }

  private Schema<?> createSuccessMetaSchema() {
    return new Schema<>()
        .type("object")
        .name("SuccessMeta")
        .addProperty("status", new Schema<>().type("integer").description("HTTP 상태 코드"))
        .addProperty("message", new Schema<>().type("string").description("응답 메시지"))
        .addProperty("timestamp", new Schema<>().type("string").description("응답 타임스탬프"))
        .addProperty("path", new Schema<>().type("string").description("요청 경로"))
        .addProperty("apiVersion", new Schema<>().type("string").description("API 버전"))
        .addProperty("serverName", new Schema<>().type("string").description("서버 이름"))
        .addProperty("requestId", new Schema<>().type("string").description("요청 ID (UUID)"));
  }

  private Schema<?> createCursorMetaSchema() {
    return new Schema<>()
        .type("object")
        .name("CursorMeta")
        .description("커서 기반 페이지네이션 메타 정보")
        .allOf(List.of(new Schema<>().$ref("#/components/schemas/SuccessMeta"))) // SuccessMeta 상속
        .addProperty("cursor", new Schema<>().type("string").description("다음 페이지 조회를 위한 커서"))
        .addProperty("size", new Schema<>().type("integer").description("페이지 당 아이템 수"))
        .addProperty("hasNext", new Schema<>().type("boolean").description("다음 페이지 존재 여부"));
  }

  private Schema<?> createSuccessResponseSchema() {
    return new Schema<>()
        .type("object")
        .name("SuccessResponse")
        .addProperty("meta", new Schema<>().$ref("#/components/schemas/SuccessMeta"))
        .addProperty("data", new Schema<>().type("object").description("API 결과 데이터").nullable(true));
  }

  private Schema<?> createCursorSuccessResponseSchema() {
    return new Schema<>()
        .type("object")
        .name("CursorSuccessResponse")
        .addProperty("meta", new Schema<>().$ref("#/components/schemas/CursorMeta"))
        .addProperty("data", new Schema<>().type("object").description("API 결과 데이터 (페이지네이션)").nullable(true));
  }

  private Schema<?> createErrorResponseSchema() {
    return new Schema<>()
        .type("object")
        .name("ErrorResponse")
        .addProperty("meta", new Schema<>().$ref("#/components/schemas/ErrorMeta"))
        .addProperty("data", new Schema<>().type("object").nullable(true));
  }

  private SecurityScheme getSecurityScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER).name("Authorization");
  }

  private SecurityRequirement getSecurityRequirement() {
    return new SecurityRequirement().addList("bearerAuth");
  }
}