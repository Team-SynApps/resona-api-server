package com.synapps.resona.report.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.report.dto.request.CommentReportRequest;
import com.synapps.resona.report.dto.request.FeedReportRequest;
import com.synapps.resona.report.dto.request.ReplyReportRequest;
import com.synapps.resona.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report", description = "신고 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 신고", description = "특정 피드를 부적절한 콘텐츠로 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed/{feedId}/report")
  public ResponseEntity<SuccessResponse<Void>> reportFeed(HttpServletRequest request,
      @Valid @RequestBody FeedReportRequest reportRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    reportService.reportFeed(reportRequest, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.REPORT_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "댓글 신고", description = "특정 댓글을 부적절한 콘텐츠로 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comment/{commentId}/report")
  public ResponseEntity<SuccessResponse<Void>> reportComment(HttpServletRequest request,
      @Valid @RequestBody CommentReportRequest reportRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    reportService.reportComment(reportRequest, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.REPORT_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 신고", description = "특정 대댓글을 부적절한 콘텐츠로 신고합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REPORT_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/reply/{replyId}/report")
  public ResponseEntity<SuccessResponse<Void>> reportReply(HttpServletRequest request,
      @Valid @RequestBody ReplyReportRequest reportRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    reportService.reportReply(reportRequest, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.REPORT_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}