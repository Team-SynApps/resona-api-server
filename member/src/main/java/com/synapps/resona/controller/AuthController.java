package com.synapps.resona.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.dto.request.auth.AppleLoginRequest;
import com.synapps.resona.dto.request.auth.LoginRequest;
import com.synapps.resona.dto.request.auth.RefreshRequest;
import com.synapps.resona.dto.response.ChatMemberDto;
import com.synapps.resona.dto.response.TokenResponse;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "LOGIN_SUCCESS", responseClass = TokenResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"LOGIN_FAILED", "PROVIDER_TYPE_MISSMATCH"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<TokenResponse>> authenticateUser(
      HttpServletRequest request,
      @RequestBody LoginRequest loginRequest) {
    TokenResponse tokenResponse = authService.login(loginRequest);
    return ResponseEntity
        .status(MemberSuccessCode.LOGIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.LOGIN_SUCCESS, createRequestInfo(request.getRequestURI()), tokenResponse));
  }

  @Operation(summary = "애플 소셜 로그인", description = "Apple ID 토큰으로 로그인하고 JWT 토큰을 발급받습니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "APPLE_LOGIN_SUCCESS", responseClass = TokenResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"PROVIDER_TYPE_MISSMATCH"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"ACCOUNT_INFO_NOT_FOUND"})
  })
  @PostMapping("/apple")
  public ResponseEntity<SuccessResponse<TokenResponse>> appleLogin(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody AppleLoginRequest appleRequest) throws Exception {
    TokenResponse tokenResponse = authService.appleLogin(appleRequest);
    return ResponseEntity
        .status(MemberSuccessCode.APPLE_LOGIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.APPLE_LOGIN_SUCCESS, createRequestInfo(request.getRequestURI()), tokenResponse));
  }

  @Operation(summary = "토큰 재발급", description = "유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "TOKEN_REFRESH_SUCCESS", responseClass = TokenResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {
          "NOT_EXPIRED",
          "REFRESH_TOKEN_NOT_FOUND",
          "INVALID_REFRESH_TOKEN"
      })
  })
  @GetMapping("/refresh-token")
  public ResponseEntity<SuccessResponse<TokenResponse>> refreshToken(
      HttpServletRequest request,
      @RequestBody RefreshRequest refreshRequest) {
    TokenResponse tokenResponse = authService.refresh(request, refreshRequest);
    return ResponseEntity
        .status(MemberSuccessCode.TOKEN_REFRESH_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.TOKEN_REFRESH_SUCCESS, createRequestInfo(request.getRequestURI()), tokenResponse));
  }

  @Operation(summary = "회원 존재 여부 확인", description = "액세스 토큰을 기반으로 현재 로그인된 사용자의 정보를 확인합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "MEMBER_INFO_SUCCESS", responseClass = ChatMemberDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {
          "TOKEN_NOT_FOUND",
          "INVALID_TOKEN",
          "EXPIRED_TOKEN"
      })
  })
  @GetMapping("/member")
  public ResponseEntity<SuccessResponse<ChatMemberDto>> memberExists(HttpServletRequest request, HttpServletResponse response) {
    ChatMemberDto memberInfo = authService.isMember(request, response);
    return ResponseEntity
        .status(MemberSuccessCode.MEMBER_INFO_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.MEMBER_INFO_SUCCESS, createRequestInfo(request.getRequestURI()), memberInfo));
  }
}