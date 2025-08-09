package synapps.resona.api.socialMedia.controller.restriction;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.service.restriction.HideService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Hide", description = "콘텐츠 숨김 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class HideController {

  private final HideService hideService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 숨김", description = "특정 피드를 숨김 처리합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "HIDE_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed/{feedId}/hide")
  public ResponseEntity<SuccessResponse<Void>> hideFeed(HttpServletRequest request,
      @PathVariable Long feedId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    hideService.hideFeed(feedId, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.HIDE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.HIDE_FEED_SUCCESS,
            createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "댓글 숨김", description = "특정 댓글을 숨김 처리합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "HIDE_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comment/{commentId}/hide")
  public ResponseEntity<SuccessResponse<Void>> hideComment(HttpServletRequest request,
      @PathVariable Long commentId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    hideService.hideComment(commentId, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.HIDE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.HIDE_COMMENT_SUCCESS,
            createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 숨김", description = "특정 대댓글을 숨김 처리합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "HIDE_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/reply/{replyId}/hide")
  public ResponseEntity<SuccessResponse<Void>> hideReply(HttpServletRequest request,
      @PathVariable Long replyId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    MemberDto memberDto = MemberDto.from(userPrincipal);
    hideService.hideReply(replyId, memberDto);
    return ResponseEntity
        .status(SocialSuccessCode.HIDE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.HIDE_REPLY_SUCCESS,
            createRequestInfo(request.getRequestURI())));
  }
}