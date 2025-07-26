package synapps.resona.api.mysql.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDetailsResponse;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.service.MemberDetailsService;

@Tag(name = "Member Details", description = "사용자 상세 개인정보 관리 API")
@RestController
@RequestMapping("/member-details")
@RequiredArgsConstructor
public class MemberDetailsController {

  private final MemberDetailsService memberDetailsService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "상세 개인정보 등록", description = "사용자의 상세 개인정보(주소, 연락처 등)를 등록합니다. 본인 또는 관리자만 접근 가능합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "개인정보 등록 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<MemberDetailsResponse>> registerPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    MemberDetailsResponse response = memberDetailsService.register(memberDetailsRequest);
    return ResponseEntity
        .status(MemberSuccessCode.REGISTER_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.REGISTER_DETAILS_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "상세 개인정보 조회", description = "현재 로그인된 사용자의 상세 개인정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "개인정보 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "개인정보를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<SuccessResponse<MemberDetailsResponse>> readPersonalInfo(HttpServletRequest request) {
    MemberDetailsResponse response = memberDetailsService.getMemberDetails();
    return ResponseEntity
        .status(MemberSuccessCode.GET_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_DETAILS_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "상세 개인정보 수정", description = "사용자의 상세 개인정보를 수정합니다. 본인 또는 관리자만 접근 가능합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "개인정보 수정 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<MemberDetails>> editPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    MemberDetails response = memberDetailsService.editMemberDetails(memberDetailsRequest);
    return ResponseEntity
        .status(MemberSuccessCode.EDIT_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.EDIT_DETAILS_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "상세 개인정보 삭제", description = "사용자의 상세 개인정보를 삭제합니다. 본인 또는 관리자만 접근 가능합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "개인정보 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deletePersonalInfo(HttpServletRequest request) {
    memberDetailsService.deleteMemberDetails();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_DETAILS_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}