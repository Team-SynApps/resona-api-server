package com.synapps.resona.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.dto.request.member_details.MemberDetailsRequest;
import com.synapps.resona.command.dto.response.MemberDetailsResponse;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.command.service.MemberDetailsService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Member Details", description = "사용자 상세 개인정보 관리 API")
@RestController
@RequestMapping("/member-details")
@RequiredArgsConstructor
public class MemberDetailsCommandController {

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