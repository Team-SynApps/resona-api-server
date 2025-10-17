package com.synapps.resona.query.controller;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.MemberSuccessCode;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.query.dto.MemberProfileDocumentDto;
import com.synapps.resona.query.service.retrieval.MemberRetrievalService;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "사용자 정보 조회 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberRetrievalController {

    private final MemberRetrievalService memberRetrievalService;
    private final ServerInfoConfig serverInfo;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "Member 목록 조회")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MemberSuccessCode.class, code = "GET_MEMBERS_SUCCESS", responseClass = CursorResult.class, listElementClass = MemberProfileDocumentDto.class))
    @ApiErrorSpec({@ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"INVALID_PARAMETER"})})
    @GetMapping
    public ResponseEntity<SuccessResponse<CursorResult<MemberProfileDocumentDto>>> getMembers(
        HttpServletRequest request,
        @Parameter(description = "다음 페이지 커서") @RequestParam(required = false) String cursor,
        @Parameter(description = "페이지 사이즈") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "국가 코드") @RequestParam(required = false) CountryCode countryCode) {
        CursorResult<MemberProfileDocumentDto> members = memberRetrievalService.getMembers(cursor, size, countryCode);
        return ResponseEntity.ok(SuccessResponse.of(MemberSuccessCode.GET_MEMBERS_SUCCESS, createRequestInfo(request.getRequestURI()), members, cursor, size, members.isHasNext()));
    }
}
