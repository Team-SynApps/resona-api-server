package synapps.resona.api.socialMedia.likes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import synapps.resona.api.oauth.entity.UserPrincipal;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.likes.dto.response.CommentLikesResponse;
import synapps.resona.api.socialMedia.likes.dto.response.FeedLikesResponse;
import synapps.resona.api.socialMedia.likes.dto.response.ReplyLikesResponse;
import synapps.resona.api.socialMedia.likes.service.LikeService;

@Tag(name = "Like", description = "좋아요 통합 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikesController {

  private final LikeService likeService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  // Feed Like

  @Operation(summary = "피드 좋아요 등록", description = "특정 피드에 좋아요를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_FEED_SUCCESS", responseClass = FeedLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "ALREADY_LIKED"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feeds/{feedId}/like")
  public ResponseEntity<SuccessResponse<FeedLikesResponse>> likeFeed(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요할 피드의 ID", required = true) @PathVariable Long feedId) {
    FeedLikesResponse response = likeService.likeFeed(feedId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "피드 좋아요 취소", description = "등록했던 피드 좋아요를 취소합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_FEED_SUCCESS", responseClass = FeedLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/feeds/{feedId}/like")
  public ResponseEntity<SuccessResponse<FeedLikesResponse>> unlikeFeed(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요를 취소할 피드의 ID", required = true) @PathVariable Long feedId) {
    FeedLikesResponse response = likeService.unlikeFeed(feedId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  // Comment Like

  @Operation(summary = "댓글 좋아요 등록", description = "특정 댓글에 좋아요를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_COMMENT_SUCCESS", responseClass = CommentLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND", "ALREADY_LIKED"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comments/{commentId}/like")
  public ResponseEntity<SuccessResponse<CommentLikesResponse>> likeComment(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentLikesResponse response = likeService.likeComment(commentId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글 좋아요 취소", description = "등록했던 댓글 좋아요를 취소합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_COMMENT_SUCCESS", responseClass = CommentLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND", "LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/comments/{commentId}/like")
  public ResponseEntity<SuccessResponse<CommentLikesResponse>> unlikeComment(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요를 취소할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentLikesResponse response = likeService.unlikeComment(commentId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  // Reply Like

  @Operation(summary = "답글 좋아요 등록", description = "특정 답글에 좋아요를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_REPLY_SUCCESS", responseClass = ReplyLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND", "ALREADY_LIKED"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/replies/{replyId}/like")
  public ResponseEntity<SuccessResponse<ReplyLikesResponse>> likeReply(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요할 답글의 ID", required = true) @PathVariable Long replyId) {
    ReplyLikesResponse response = likeService.likeReply(replyId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "답글 좋아요 취소", description = "등록했던 답글 좋아요를 취소합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_REPLY_SUCCESS", responseClass = ReplyLikesResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND", "LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/replies/{replyId}/like")
  public ResponseEntity<SuccessResponse<ReplyLikesResponse>> unlikeReply(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "좋아요를 취소할 답글의 ID", required = true) @PathVariable Long replyId) {
    ReplyLikesResponse response = likeService.unlikeReply(replyId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }
}
