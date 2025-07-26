package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
import synapps.resona.api.mysql.member.dto.request.auth.AppleLoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.LoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RefreshRequest;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.service.AuthService;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "일반 로그인", description = "이메일과 비밀번호를 사용하여 로그인하고 JWT 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "401", description = "로그인 실패 (계정 정보 불일치)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<TokenResponse>> authenticateUser(
      HttpServletRequest request,
      @RequestBody LoginRequest loginRequest) {
    TokenResponse tokenResponse = authService.login(loginRequest);
    return ResponseEntity
        .status(MemberSuccessCode.LOGIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.LOGIN_SUCCESS, createRequestInfo(request.getQueryString()), tokenResponse));
  }

  @Operation(summary = "애플 소셜 로그인", description = "Apple ID 토큰으로 로그인하고 JWT 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "애플 로그인 성공",
          content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/apple")
  public ResponseEntity<SuccessResponse<TokenResponse>> appleLogin(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody AppleLoginRequest appleRequest) throws Exception {
    TokenResponse tokenResponse = authService.appleLogin(request, response, appleRequest);
    return ResponseEntity
        .status(MemberSuccessCode.APPLE_LOGIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.APPLE_LOGIN_SUCCESS, createRequestInfo(request.getQueryString()), tokenResponse));
  }

  @Operation(summary = "토큰 재발급", description = "유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
          content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/refresh-token")
  public ResponseEntity<SuccessResponse<TokenResponse>> refreshToken(
      HttpServletRequest request,
      @RequestBody RefreshRequest refreshRequest) {
    TokenResponse tokenResponse = authService.refresh(request, refreshRequest);
    return ResponseEntity
        .status(MemberSuccessCode.TOKEN_REFRESH_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.TOKEN_REFRESH_SUCCESS, createRequestInfo(request.getQueryString()), tokenResponse));
  }

  @Operation(summary = "회원 존재 여부 확인", description = "액세스 토큰을 기반으로 현재 로그인된 사용자의 정보를 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  @GetMapping("/member")
  public ResponseEntity<?> memberExists(HttpServletRequest request, HttpServletResponse response) {
    return ResponseEntity
        .status(MemberSuccessCode.MEMBER_INFO_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.MEMBER_INFO_SUCCESS, createRequestInfo(request.getQueryString()), authService.isMember(request, response)));
  }

//  @ExceptionHandler(AuthenticationException.class)
//  public ResponseEntity<ErrorResponse<Map<String, String>>> handleDataValidationException(
//      AuthenticationException ex,
//      HttpServletRequest request
//  ) {
//    return ResponseEntity
//        .status(GlobalErrorCode.LOGIN_FAILED.getStatus())
//        .body(ErrorResponse.of(GlobalErrorCode.LOGIN_FAILED, createRequestInfo(request.getQueryString()), Map.of("validationErrors", ex.getMessage())));
//  }
}