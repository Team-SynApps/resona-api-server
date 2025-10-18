package com.synapps.resona.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.dto.MemberDto;
import com.synapps.resona.command.dto.request.auth.LoginRequest;
import com.synapps.resona.command.dto.request.auth.RegisterRequest;
import com.synapps.resona.command.dto.request.member.MemberPasswordChangeDto;
import com.synapps.resona.command.dto.response.JoinResponseDto;
import com.synapps.resona.command.dto.response.MemberRegisterResponseDto;
import com.synapps.resona.command.dto.response.TokenResponse;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.command.service.AuthService;
import com.synapps.resona.command.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "사용자 정보 관리 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final AuthService authService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "회원가입 및 자동 로그인", description = "회원가입 처리 후 즉시 로그인하여 토큰을 발급합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "JOIN_SUCCESS", responseClass = JoinResponseDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "DUPLICATE_EMAIL", "DUPLICATE_TAG", "ACCOUNT_INFO_NOT_FOUND", "UNAUTHENTICATED_REQUEST"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"PROVIDER_TYPE_MISSMATCH"})
  })
  @PostMapping("/join")
  public ResponseEntity<SuccessResponse<JoinResponseDto>> join(HttpServletRequest request, @Valid @RequestBody RegisterRequest registerRequest) {
    MemberRegisterResponseDto memberRegisterResponseDto = memberService.signUp(registerRequest);
    LoginRequest loginRequest = new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword());
    TokenResponse tokenResponse = authService.login(loginRequest);
    JoinResponseDto responseData = JoinResponseDto.of(memberRegisterResponseDto, tokenResponse);
    return ResponseEntity
        .status(MemberSuccessCode.JOIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.JOIN_SUCCESS, createRequestInfo(request.getRequestURI()), responseData));
  }

  @Operation(summary = "내 기본 정보 조회", description = "현재 로그인된 사용자의 기본 정보를 조회합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_MY_INFO_SUCCESS", responseClass = MemberDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"INVALID_TOKEN", "EXPIRED_TOKEN"})
  })
  @GetMapping("/info")
  public ResponseEntity<SuccessResponse<MemberDto>> getUser(
      HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    return ResponseEntity
        .status(MemberSuccessCode.GET_MY_INFO_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_MY_INFO_SUCCESS, createRequestInfo(request.getRequestURI()), memberInfo));
  }


  @Operation(summary = "비밀번호 변경", description = "현재 로그인된 사용자의 비밀번호를 변경합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "PASSWORD_CHANGE_SUCCESS")) // responseClass 생략 (Void)
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "UNAUTHENTICATED_REQUEST", "INVALID_PASSWORD"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"INVALID_TOKEN"})
  })
  @PostMapping("/password")
  public ResponseEntity<SuccessResponse<Void>> changePassword(HttpServletRequest request, @RequestBody MemberPasswordChangeDto requestBody) {
    memberService.changePassword(request, requestBody);
    return ResponseEntity
        .status(MemberSuccessCode.PASSWORD_CHANGE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.PASSWORD_CHANGE_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 비활성화 처리합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "DELETE_USER_SUCCESS")) // responseClass 생략 (Void)
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"INVALID_TOKEN"})
  })
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<Void>> deleteUser(HttpServletRequest request) {
    memberService.deleteUser();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_USER_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_USER_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}