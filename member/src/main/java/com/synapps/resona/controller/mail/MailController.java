package com.synapps.resona.controller.mail;

import com.synapps.resona.email.EmailCheckDto;
import com.synapps.resona.email.MailService;
import com.synapps.resona.email.code.EmailSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.ErrorResponse;
import com.synapps.resona.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email", description = "이메일 인증 API")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {

  private final MailService mailService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "인증 이메일 발송", description = "입력된 이메일 주소로 인증번호를 발송합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 발송 성공 (남은 발송 횟수 포함)"),
      @ApiResponse(responseCode = "429", description = "일일 발송 횟수 초과",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping()
  public ResponseEntity<SuccessResponse<HashMap<String, Object>>> sendMail(HttpServletRequest request,
      @Parameter(description = "인증번호를 받을 이메일 주소", required = true) @RequestParam String mail) {
    HashMap<String, Object> result = mailService.send(mail);

    return ResponseEntity
        .status(EmailSuccessCode.SEND_VERIFICATION_EMAIL_SUCCESS.getStatus())
        .body(SuccessResponse.of(EmailSuccessCode.SEND_VERIFICATION_EMAIL_SUCCESS, createRequestInfo(request.getQueryString()), result));
  }

  @Operation(summary = "이메일 인증 및 임시 토큰 발급", description = "전달받은 인증번호를 확인하고, 일치할 경우 임시 토큰을 발급합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 인증 성공 및 임시 토큰 발급"),
      @ApiResponse(responseCode = "406", description = "인증번호 불일치",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/temp-token")
  public ResponseEntity<SuccessResponse<?>> mailCheckAndIssueToken(HttpServletRequest request,
      @Valid @RequestBody EmailCheckDto emailCheckDto) {
    return ResponseEntity
        .status(EmailSuccessCode.EMAIL_VERIFICATION_SUCCESS.getStatus())
        .body(SuccessResponse.of(EmailSuccessCode.EMAIL_VERIFICATION_SUCCESS, createRequestInfo(request.getQueryString()), mailService.verifyMailAndIssueToken(emailCheckDto.getEmail(), emailCheckDto.getNumber())));
  }
}