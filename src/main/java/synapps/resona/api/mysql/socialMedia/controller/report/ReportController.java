package synapps.resona.api.mysql.socialMedia.controller.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.mysql.member.code.AuthErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.socialMedia.code.SocialErrorCode;
import synapps.resona.api.mysql.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.mysql.socialMedia.dto.report.request.CommentReportRequest;
import synapps.resona.api.mysql.socialMedia.dto.report.request.FeedReportRequest;
import synapps.resona.api.mysql.socialMedia.dto.report.request.ReplyReportRequest;
import synapps.resona.api.mysql.socialMedia.dto.report.request.ReportRequest;
import synapps.resona.api.mysql.socialMedia.dto.report.response.CommentReportResponse;
import synapps.resona.api.mysql.socialMedia.dto.report.response.ReplyReportResponse;
import synapps.resona.api.mysql.socialMedia.service.report.ReportService;
import synapps.resona.api.oauth.entity.UserPrincipal;

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