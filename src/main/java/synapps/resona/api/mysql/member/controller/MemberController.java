package synapps.resona.api.mysql.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.dto.request.auth.LoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.service.AuthService;
import synapps.resona.api.mysql.member.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final AuthService authService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  // TODO: 커스텀 어노테이션으로 클래스 설정만 해줄 수 있게 하는 코드가 필요해보임
  @Operation(summary = "회원 등록", description = "회원 등록 후 응답 DTO 반환")
  @ApiResponse(
      responseCode = "200",
      description = "회원 등록 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = MemberRegisterResponseDto.class)
      )
  )
  @PostMapping("/join")
  public ResponseEntity<?> join(HttpServletRequest request,
      @Valid @RequestBody RegisterRequest registerRequest) {
    Meta metaData = createSuccessMetaData(request.getQueryString());

    MemberRegisterResponseDto memberRegisterResponseDto = memberService.signUp(registerRequest);

    LoginRequest loginRequest = new LoginRequest(registerRequest.getEmail(),
        registerRequest.getPassword());
    TokenResponse tokenResponse = authService.login(loginRequest);

    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberRegisterResponseDto, tokenResponse));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/info")
  public ResponseEntity<?> getUser(HttpServletRequest request) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.getMember()));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/detail")
  public ResponseEntity<?> getMemberDetailInfo(HttpServletRequest request) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(memberService.getMemberDetailInfo()));
    return ResponseEntity.ok(responseData);
  }

  @PostMapping("/password")
  public ResponseEntity<?> changePassword(HttpServletRequest request,
      @RequestBody MemberPasswordChangeDto requestBody) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseDto = new ResponseDto(metaData,
        List.of(memberService.changePassword(request, requestBody)));
    return ResponseEntity.ok(responseDto);
  }

  @DeleteMapping()
  public ResponseEntity<?> deleteUser(HttpServletRequest request) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(memberService.deleteUser()));
    return ResponseEntity.ok(responseData);
  }

}