package com.synapps.resona.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.dto.MemberDto;
import com.synapps.resona.command.dto.request.profile.ProfileRequest;
import com.synapps.resona.common.dto.ProfileResponse;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.command.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Profile", description = "사용자 프로필 관리 API")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileCommandController {

  private final ProfileService profileService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "프로필 등록", description = "사용자의 프로필을 등록합니다. (인증 필요)")
  @ApiSuccessResponse(value = @SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "REGISTER_PROFILE_SUCCESS", responseClass = ProfileResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND", "TIMESTAMP_INVALID"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> registerProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    ProfileResponse response = profileService.register(profileRequest, memberInfo);
    return ResponseEntity
        .status(MemberSuccessCode.REGISTER_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.REGISTER_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "프로필 수정", description = "사용자의 프로필을 수정합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "EDIT_PROFILE_SUCCESS", responseClass = ProfileResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND", "TIMESTAMP_INVALID"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PutMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> editProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal
      ) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    ProfileResponse response = profileService.editProfile(profileRequest, memberInfo);
    return ResponseEntity
        .status(MemberSuccessCode.EDIT_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.EDIT_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "프로필 삭제", description = "사용자의 프로필을 삭제합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "DELETE_PROFILE_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping
  public ResponseEntity<SuccessResponse<Void>> deleteProfile(
      HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    profileService.deleteProfile(memberInfo);
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}