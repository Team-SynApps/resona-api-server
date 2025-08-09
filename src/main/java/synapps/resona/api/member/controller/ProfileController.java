package synapps.resona.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.code.MemberSuccessCode;
import synapps.resona.api.member.dto.request.profile.DuplicateTagRequest;
import synapps.resona.api.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.member.dto.response.ProfileResponse;
import synapps.resona.api.member.service.ProfileService;

@Tag(name = "Member Profile", description = "사용자 프로필 관리 API")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "프로필 등록", description = "사용자의 프로필을 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "REGISTER_PROFILE_SUCCESS", responseClass = ProfileResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND", "TIMESTAMP_INVALID"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> registerProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    ProfileResponse response = profileService.register(profileRequest);
    return ResponseEntity
        .status(MemberSuccessCode.REGISTER_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.REGISTER_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "프로필 조회", description = "현재 로그인된 사용자의 프로필을 조회합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_PROFILE_SUCCESS", responseClass = ProfileResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @GetMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> readProfile(HttpServletRequest request) {
    ProfileResponse response = profileService.readProfile();
    return ResponseEntity
        .status(MemberSuccessCode.GET_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "프로필 수정", description = "사용자의 프로필을 수정합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "EDIT_PROFILE_SUCCESS", responseClass = ProfileResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND", "TIMESTAMP_INVALID"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PutMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> editProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    ProfileResponse response = profileService.editProfile(profileRequest);
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
  public ResponseEntity<SuccessResponse<Void>> deleteProfile(HttpServletRequest request) {
    profileService.deleteProfile();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "프로필 태그 중복 확인", description = "회원가입 또는 프로필 수정 시 사용할 태그의 중복 여부를 확인합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "CHECK_TAG_DUPLICATE_SUCCESS", responseClass = Boolean.class))
  @PostMapping("/duplicate-tag")
  public ResponseEntity<SuccessResponse<Boolean>> checkDuplicateId(HttpServletRequest request,
      @RequestBody DuplicateTagRequest duplicateTagRequest) {
    boolean response = profileService.checkDuplicateTag(duplicateTagRequest.getTag());
    return ResponseEntity
        .status(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }
}