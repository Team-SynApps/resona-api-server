package com.synapps.resona.query.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.dto.request.profile.DuplicateTagRequest;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.query.dto.ProfileQueryResponseDto;
import com.synapps.resona.query.service.ProfileQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Profile Query", description = "사용자 프로필 조회 API")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileQueryController {

    private final ProfileQueryService profileQueryService;
    private final ServerInfoConfig serverInfo;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "프로필 조회", description = "현재 로그인된 사용자의 프로필을 조회합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_PROFILE_SUCCESS", responseClass = ProfileQueryResponseDto.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"PROFILE_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<ProfileQueryResponseDto>> readProfile(
        HttpServletRequest request,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProfileQueryResponseDto response = profileQueryService.readProfile(userPrincipal.getEmail());
        return ResponseEntity
            .status(MemberSuccessCode.GET_PROFILE_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_PROFILE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
    }

    @Operation(summary = "프로필 태그 중복 확인", description = "회원가입 또는 프로필 수정 시 사용할 태그의 중복 여부를 확인합니다.")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "CHECK_TAG_DUPLICATE_SUCCESS", responseClass = Boolean.class))
    @PostMapping("/duplicate-tag")
    public ResponseEntity<SuccessResponse<Boolean>> checkDuplicateId(HttpServletRequest request,
        @RequestBody DuplicateTagRequest duplicateTagRequest) {
        boolean response = profileQueryService.checkDuplicateTag(duplicateTagRequest.getTag());
        return ResponseEntity
            .status(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.CHECK_TAG_DUPLICATE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
    }
}
