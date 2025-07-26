package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
import synapps.resona.api.mysql.member.dto.request.auth.LoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.dto.response.MemberInfoDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.service.AuthService;
import synapps.resona.api.mysql.member.service.MemberService;

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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원가입 및 로그인 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/join")
  public ResponseEntity<SuccessResponse<Map<String, Object>>> join(HttpServletRequest request,
      @Valid @RequestBody RegisterRequest registerRequest) {

    MemberRegisterResponseDto memberRegisterResponseDto = memberService.signUp(registerRequest);
    LoginRequest loginRequest = new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword());
    TokenResponse tokenResponse = authService.login(loginRequest);

    Map<String, Object> responseData = Map.of(
        "memberInfo", memberRegisterResponseDto,
        "tokenInfo", tokenResponse
    );

    return ResponseEntity
        .status(MemberSuccessCode.JOIN_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.JOIN_SUCCESS, createRequestInfo(request.getQueryString()), responseData));
  }

  @Operation(summary = "내 기본 정보 조회", description = "현재 로그인된 사용자의 기본 정보를 조회합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = MemberDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/info")
  public ResponseEntity<SuccessResponse<MemberDto>> getUser(HttpServletRequest request) {
    MemberDto memberInfo = memberService.getMember();
    return ResponseEntity
        .status(MemberSuccessCode.GET_MY_INFO_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_MY_INFO_SUCCESS, createRequestInfo(request.getQueryString()), memberInfo));
  }

  @Operation(summary = "내 상세 정보 조회", description = "현재 로그인된 사용자의 상세 정보를 조회합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = MemberInfoDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/detail")
  public ResponseEntity<SuccessResponse<MemberInfoDto>> getMemberDetailInfo(HttpServletRequest request) {
    MemberInfoDto memberDetailInfo = memberService.getMemberDetailInfo();
    return ResponseEntity
        .status(MemberSuccessCode.GET_MEMBER_DETAIL_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_MEMBER_DETAIL_SUCCESS, createRequestInfo(request.getQueryString()), memberDetailInfo));
  }

  @Operation(summary = "비밀번호 변경", description = "현재 로그인된 사용자의 비밀번호를 변경합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
      @ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/password")
  public ResponseEntity<SuccessResponse<Void>> changePassword(HttpServletRequest request,
      @RequestBody MemberPasswordChangeDto requestBody) {
    memberService.changePassword(request, requestBody);
    return ResponseEntity
        .status(MemberSuccessCode.PASSWORD_CHANGE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.PASSWORD_CHANGE_SUCCESS, createRequestInfo(request.getQueryString())));
  }

  @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 비활성화 처리합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<Void>> deleteUser(HttpServletRequest request) {
    memberService.deleteUser();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_USER_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_USER_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}