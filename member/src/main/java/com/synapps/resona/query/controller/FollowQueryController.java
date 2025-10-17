package com.synapps.resona.query.controller;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.query.dto.MemberProfileQueryDto;
import com.synapps.resona.query.service.retrieval.FollowQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Follow", description = "팔로우/언팔로우 목록 조회 API")
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowQueryController {

    private final FollowQueryService followQueryService;
    private final ServerInfoConfig serverInfo;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "팔로워 목록 조회", description = "특정 사용자의 팔로워 목록을 조회합니다.")
    @Parameter(name = "memberId", description = "조회할 사용자의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_FOLLOWERS_SUCCESS", listElementClass = MemberProfileQueryDto.class))
    @ApiErrorSpec({@ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})})
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<SuccessResponse<List<MemberProfileQueryDto>>> getFollowers(@PathVariable Long memberId, HttpServletRequest request) {
        List<MemberProfileQueryDto> followers = followQueryService.getFollowers(memberId);
        return ResponseEntity
            .status(MemberSuccessCode.GET_FOLLOWERS_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_FOLLOWERS_SUCCESS, createRequestInfo(request.getRequestURI()), followers));
    }

    @Operation(summary = "팔로잉 목록 조회", description = "특정 사용자의 팔로잉 목록을 조회합니다.")
    @Parameter(name = "memberId", description = "조회할 사용자의 ID", required = true, in = ParameterIn.PATH)
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_FOLLOWINGS_SUCCESS", listElementClass = MemberProfileQueryDto.class))
    @ApiErrorSpec({@ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})})
    @GetMapping("/{memberId}/followings")
    public ResponseEntity<SuccessResponse<List<MemberProfileQueryDto>>> getFollowings(@PathVariable Long memberId, HttpServletRequest request) {
        List<MemberProfileQueryDto> followings = followQueryService.getFollowings(memberId);
        return ResponseEntity
            .status(MemberSuccessCode.GET_FOLLOWINGS_SUCCESS.getStatus())
            .body(SuccessResponse.of(MemberSuccessCode.GET_FOLLOWINGS_SUCCESS, createRequestInfo(request.getRequestURI()), followings));
    }
}
