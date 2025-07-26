package synapps.resona.api.mysql.socialMedia.controller.complaint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import synapps.resona.api.mysql.socialMedia.code.SocialErrorCode;
import synapps.resona.api.mysql.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.mysql.socialMedia.dto.complaint.FeedComplaintRequest;
import synapps.resona.api.mysql.socialMedia.service.complaint.FeedComplaintService;

@Tag(name = "Complaint", description = "신고 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedComplaintsController {

  private final FeedComplaintService feedComplaintService;
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
  @PostMapping("/feed/{feedId}/complaint")
  public ResponseEntity<SuccessResponse<Void>> reportFeed(HttpServletRequest request,
      @Parameter(description = "신고할 피드의 ID", required = true) @PathVariable Long feedId,
      @Valid @RequestBody FeedComplaintRequest complaintRequest) {
    feedComplaintService.reportFeed(feedId, complaintRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REPORT_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REPORT_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}