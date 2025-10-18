package com.synapps.resona.comment.adapter.in.web;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.comment.command.service.CommentLikeService;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Comment", description = "댓글/대댓글 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentLikeController {

  private final CommentLikeService commentLikeService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "댓글 좋아요")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comments/{commentId}/like")
  public ResponseEntity<SuccessResponse<Void>> likeComment(
      HttpServletRequest request,
      @PathVariable Long commentId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentLikeService.likeComment(user.getMemberId(), commentId);
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "댓글 좋아요 취소")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/comments/{commentId}/like")
  public ResponseEntity<SuccessResponse<Void>> unlikeComment(
      HttpServletRequest request,
      @PathVariable Long commentId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentLikeService.unlikeComment(user.getMemberId(), commentId);
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 좋아요")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/replies/{replyId}/like")
  public ResponseEntity<SuccessResponse<Void>> likeReply(
      HttpServletRequest request,
      @PathVariable Long replyId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentLikeService.likeReply(user.getMemberId(), replyId);
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 좋아요 취소")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/replies/{replyId}/like")
  public ResponseEntity<SuccessResponse<Void>> unlikeReply(
      HttpServletRequest request,
      @PathVariable Long replyId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentLikeService.unlikeReply(user.getMemberId(), replyId);
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}
