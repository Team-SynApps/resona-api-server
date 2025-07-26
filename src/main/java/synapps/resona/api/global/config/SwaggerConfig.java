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
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.dto.code.ErrorCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

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
      ApiErrorSpec apiErrorSpec = handlerMethod.getMethodAnnotation(ApiErrorSpec.class);
      if (apiErrorSpec == null) {
        return operation;
      }
      generateErrorResponseDocs(operation, apiErrorSpec.value());
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