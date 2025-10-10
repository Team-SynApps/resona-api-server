package com.synapps.resona.comment.adapter.in;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.comment.command.service.CommentCommandService;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.CommentRequest;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.dto.ReplyRequest;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment Command", description = "댓글/대댓글 생성, 수정, 삭제 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentCommandController {

  private final CommentCommandService commentCommandService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "댓글 등록", description = "피드에 새로운 댓글을 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_COMMENT_SUCCESS", responseClass = CommentDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comments")
  public ResponseEntity<SuccessResponse<CommentDto>> registerComment(HttpServletRequest request,
      @Valid @RequestBody CommentRequest commentRequest,
      @AuthenticationPrincipal AuthenticatedUser member) {
    CommentDto response = commentCommandService.createComment(member.getMemberId(), commentRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "대댓글 등록", description = "특정 댓글에 대한 대댓글을 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_REPLY_SUCCESS", responseClass = ReplyDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/replies")
  public ResponseEntity<SuccessResponse<ReplyDto>> registerReply(HttpServletRequest request,
      @Valid @RequestBody ReplyRequest replyRequest,
      @AuthenticationPrincipal AuthenticatedUser member) {
    ReplyDto response = commentCommandService.createReply(member.getMemberId(), replyRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다. (작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<SuccessResponse<Void>> deleteComment(
      HttpServletRequest request,
      @PathVariable Long commentId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentCommandService.deleteComment(user.getMemberId(), commentId);
    return ResponseEntity.ok(SuccessResponse.of(SocialSuccessCode.DELETE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "대댓글 삭제", description = "특정 대댓글을 삭제합니다. (작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/replies/{replyId}")
  public ResponseEntity<SuccessResponse<Void>> deleteReply(
      HttpServletRequest request,
      @PathVariable Long replyId,
      @AuthenticationPrincipal AuthenticatedUser user
  ) {
    commentCommandService.deleteReply(user.getMemberId(), replyId);
    return ResponseEntity.ok(SuccessResponse.of(SocialSuccessCode.DELETE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }


}