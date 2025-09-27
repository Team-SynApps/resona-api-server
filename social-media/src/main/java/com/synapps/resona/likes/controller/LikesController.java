package com.synapps.resona.likes.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.member.UserPrincipal;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.likes.dto.response.CommentLikesResponse;
import com.synapps.resona.likes.dto.response.FeedLikesResponse;
import com.synapps.resona.likes.dto.response.ReplyLikesResponse;
import com.synapps.resona.likes.service.LikeService;
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요할 피드의 ID", required = true) @PathVariable Long feedId) {
    FeedLikesResponse response = likeService.likeFeed(feedId, user.getMemberId());
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요를 취소할 피드의 ID", required = true) @PathVariable Long feedId) {
    FeedLikesResponse response = likeService.unlikeFeed(feedId, user.getMemberId());
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentLikesResponse response = likeService.likeComment(commentId, user.getMemberId());
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요를 취소할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentLikesResponse response = likeService.unlikeComment(commentId, user.getMemberId());
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요할 답글의 ID", required = true) @PathVariable Long replyId) {
    ReplyLikesResponse response = likeService.likeReply(replyId, user.getMemberId());
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "좋아요를 취소할 답글의 ID", required = true) @PathVariable Long replyId) {
    ReplyLikesResponse response = likeService.unlikeReply(replyId, user.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }
}
