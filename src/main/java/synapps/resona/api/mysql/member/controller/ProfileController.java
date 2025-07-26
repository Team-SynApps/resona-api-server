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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.response.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.mysql.member.code.MemberSuccessCode;
import synapps.resona.api.mysql.member.dto.request.profile.DuplicateTagRequest;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.dto.response.ProfileResponse;
import synapps.resona.api.mysql.member.service.ProfileService;

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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "프로필 등록 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> registerProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    ProfileResponse response = profileService.register(profileRequest);
    return ResponseEntity
        .status(MemberSuccessCode.REGISTER_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.REGISTER_PROFILE_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "프로필 조회", description = "현재 로그인된 사용자의 프로필을 조회합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> readProfile(HttpServletRequest request) {
    ProfileResponse response = profileService.readProfile();
    return ResponseEntity
        .status(MemberSuccessCode.GET_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.GET_PROFILE_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "프로필 수정", description = "사용자의 프로필을 수정합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping
  public ResponseEntity<SuccessResponse<ProfileResponse>> editProfile(HttpServletRequest request,
      @Valid @RequestBody ProfileRequest profileRequest) {
    ProfileResponse response = profileService.editProfile(profileRequest);
    return ResponseEntity
        .status(MemberSuccessCode.EDIT_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.EDIT_PROFILE_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "프로필 삭제", description = "사용자의 프로필을 삭제합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping
  public ResponseEntity<SuccessResponse<Void>> deleteProfile(HttpServletRequest request) {
    profileService.deleteProfile();
    return ResponseEntity
        .status(MemberSuccessCode.DELETE_PROFILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.DELETE_PROFILE_SUCCESS, createRequestInfo(request.getQueryString())));
  }

  @Operation(summary = "프로필 태그 중복 확인", description = "회원가입 또는 프로필 수정 시 사용할 태그의 중복 여부를 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "중복 검사 성공 (isDuplicate: true/false)")
  })
  @PostMapping("/duplicate-tag")
  public ResponseEntity<SuccessResponse<Boolean>> checkDuplicateId(HttpServletRequest request,
      @RequestBody DuplicateTagRequest duplicateTagRequest) {
    boolean response = profileService.checkDuplicateTag(duplicateTagRequest.getTag());
    return ResponseEntity
        .status(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS.getStatus())
        .body(SuccessResponse.of(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }
}