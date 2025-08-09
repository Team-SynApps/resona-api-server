package synapps.resona.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse; // 추가
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec; // 추가
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.code.MemberSuccessCode;
import synapps.resona.api.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.member.dto.response.MemberDetailsResponse;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.service.MemberDetailsService;

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
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "REGISTER_DETAILS_SUCCESS", responseClass = MemberDetailsResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_DETAILS_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @PostMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<MemberDetailsResponse>> registerPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    MemberDetailsResponse response = memberDetailsService.register(memberDetailsRequest);
    return ResponseEntity
        .status(MemberSuccessCode.REGISTER_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.REGISTER_DETAILS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "상세 개인정보 조회", description = "현재 로그인된 사용자의 상세 개인정보를 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_DETAILS_SUCCESS", responseClass = MemberDetailsResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_DETAILS_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @GetMapping
  public ResponseEntity<SuccessResponse<MemberDetailsResponse>> readPersonalInfo(HttpServletRequest request) {
    MemberDetailsResponse response = memberDetailsService.getMemberDetails();
    return ResponseEntity
        .status(MemberSuccessCode.GET_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_DETAILS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "상세 개인정보 수정", description = "사용자의 상세 개인정보를 수정합니다. 본인 또는 관리자만 접근 가능합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "EDIT_DETAILS_SUCCESS", responseClass = MemberDetails.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_DETAILS_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @PutMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<MemberDetails>> editPersonalInfo(HttpServletRequest request,
      @Valid @RequestBody MemberDetailsRequest memberDetailsRequest) {
    MemberDetails response = memberDetailsService.editMemberDetails(memberDetailsRequest);
    return ResponseEntity
        .status(MemberSuccessCode.EDIT_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.EDIT_DETAILS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "상세 개인정보 삭제", description = "사용자의 상세 개인정보를 삭제합니다. 본인 또는 관리자만 접근 가능합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "DELETE_DETAILS_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_DETAILS_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping
  @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deletePersonalInfo(HttpServletRequest request) {
    memberDetailsService.deleteMemberDetails();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_DETAILS_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_DETAILS_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}