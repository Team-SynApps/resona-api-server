package com.synapps.resona.query.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.common.dto.MemberDetailsResponse;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.query.dto.MemberDetailQueryDto;
import com.synapps.resona.query.dto.MemberDetailsQueryDto;
import com.synapps.resona.query.service.retrieval.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "사용자 정보 조회 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberQueryController {

    private final MemberQueryService memberQueryService;
    private final ServerInfoConfig serverInfo;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "내 상세 정보 조회", description = "현재 로그인된 사용자의 상세 정보를 조회합니다. (인증 필요)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_MEMBER_DETAIL_SUCCESS", responseClass = MemberDetailQueryDto.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"INVALID_TOKEN", "EXPIRED_TOKEN"})
    })
    @GetMapping("/detail")
    public ResponseEntity<SuccessResponse<MemberDetailsResponse>> getMemberDetailInfo(
        HttpServletRequest request,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberDetailsResponse memberDetailInfo = memberQueryService.getMemberDetailInfo(userPrincipal.getEmail());
        return ResponseEntity
            .status(MemberSuccessCode.GET_MEMBER_DETAIL_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_MEMBER_DETAIL_SUCCESS, createRequestInfo(request.getRequestURI()), memberDetailInfo));
    }

    @Tag(name = "Member Details")
    @Operation(summary = "상세 개인정보 조회", description = "현재 로그인된 사용자의 상세 개인정보를 조회합니다.")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_DETAILS_SUCCESS", responseClass = MemberDetailsQueryDto.class))
    @ApiErrorSpec({
        @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_DETAILS_NOT_FOUND"}),
        @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
    })
    @GetMapping("/details")
    public ResponseEntity<SuccessResponse<MemberDetailsResponse>> readPersonalInfo(HttpServletRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberDetailsResponse response = memberQueryService.getMemberDetails(userPrincipal.getEmail());
        return ResponseEntity
            .status(MemberSuccessCode.GET_DETAILS_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_DETAILS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
    }
}
