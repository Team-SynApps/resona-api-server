package com.synapps.resona.report.command.controller;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.report.code.ReportErrorCode;
import com.synapps.resona.report.code.ReportSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.report.command.service.UnifiedReportService;
import com.synapps.resona.report.dto.request.UnifiedReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report", description = "콘텐츠 신고 등록 및 처리 API")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class UnifiedReportController {

    private final UnifiedReportService unifiedReportService;
    private final MemberRepository memberRepository;
    private final ServerInfoConfig serverInfoConfig;

    @Operation(summary = "신고 등록", description = "피드, 댓글, 대댓글, 멤버를 신고합니다.")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = ReportSuccessCode.class, code = "REPORT_SUCCESS"))
    @ApiErrorSpec({
            @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"}),
            @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
            @ErrorCodeSpec(enumClass = ReportErrorCode.class, codes = {"ALREADY_REPORTED"}),
            @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "COMMENT_NOT_FOUND", "REPLY_NOT_FOUND"})
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> report(
            HttpServletRequest servletRequest,
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UnifiedReportRequest request) {
        Object result = unifiedReportService.report(
                memberRepository.findById(user.getMemberId()).orElseThrow(MemberException::memberNotFound),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(ReportSuccessCode.REPORT_SUCCESS, new RequestInfo(serverInfoConfig.getApiVersion(), serverInfoConfig.getServerName(), servletRequest.getRequestURI()), result));
    }
}
