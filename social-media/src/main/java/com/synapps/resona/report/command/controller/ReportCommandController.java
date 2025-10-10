package com.synapps.resona.report.command.controller;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.report.command.service.CommentReportService;
import com.synapps.resona.report.command.service.FeedReportService;
import com.synapps.resona.report.command.service.ReplyReportService;
import com.synapps.resona.report.command.service.ReportManagementService;
import com.synapps.resona.report.dto.request.ReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report Command", description = "콘텐츠 신고 등록 및 처리 API")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportCommandController {

  private final FeedReportService feedReportService;
  private final CommentReportService commentReportService;
  private final ReplyReportService replyReportService;
  private final ServerInfoConfig serverInfo;
  private final ReportManagementService reportManagementService;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 신고", description = "특정 피드를 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "ALREADY_REPORTED"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed/{feedId}")
  public ResponseEntity<SuccessResponse<Void>> reportFeed(
      HttpServletRequest request,
      @Parameter(description = "신고할 피드의 ID", required = true) @PathVariable Long feedId,
      @Valid @RequestBody ReportRequest reportRequest,
      @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
  ) {
    feedReportService.reportFeed(user.getMemberId(), feedId, reportRequest.getReportCategory(), reportRequest.isBlocked());
    return ResponseEntity.status(SocialSuccessCode.REPORT_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "댓글 신고", description = "특정 댓글을 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND", "ALREADY_REPORTED"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comment/{commentId}")
  public ResponseEntity<SuccessResponse<Void>> reportComment(
      HttpServletRequest request,
      @Parameter(description = "신고할 댓글의 ID", required = true) @PathVariable Long commentId,
      @Valid @RequestBody ReportRequest reportRequest,
      @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentReportService.reportComment(user.getMemberId(), commentId, reportRequest.getReportCategory(), reportRequest.isBlocked());
    return ResponseEntity.status(SocialSuccessCode.REPORT_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 신고", description = "특정 대댓글을 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND", "ALREADY_REPORTED"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/reply/{replyId}")
  public ResponseEntity<SuccessResponse<Void>> reportReply(
      HttpServletRequest request,
      @Parameter(description = "신고할 대댓글의 ID", required = true) @PathVariable Long replyId,
      @Valid @RequestBody ReportRequest reportRequest,
      @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
  ) {
    replyReportService.reportReply(user.getMemberId(), replyId, reportRequest.getReportCategory(), reportRequest.isBlocked());
    return ResponseEntity.status(SocialSuccessCode.REPORT_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "신고 승인 처리", description = "대기 중인 신고를 승인 상태로 변경합니다. (관리자 권한 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "RESOLVE_REPORT_SUCCESS"))
  @PatchMapping("/{reportId}/resolve")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> resolveReport(HttpServletRequest request, @PathVariable Long reportId) {
    reportManagementService.resolveReport(reportId);
    return ResponseEntity.status(SocialSuccessCode.RESOLVE_REPORT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.RESOLVE_REPORT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "신고 반려 처리", description = "대기 중인 신고를 반려 상태로 변경합니다. (관리자 권한 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REJECT_REPORT_SUCCESS"))
  @PatchMapping("/{reportId}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> rejectReport(HttpServletRequest request, @PathVariable Long reportId) {
    reportManagementService.rejectReport(reportId);
    return ResponseEntity.status(SocialSuccessCode.REJECT_REPORT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REJECT_REPORT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}
