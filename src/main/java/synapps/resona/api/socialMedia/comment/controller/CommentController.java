package synapps.resona.api.socialMedia.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.comment.dto.CommentDto;
import synapps.resona.api.socialMedia.comment.dto.request.CommentRequest;
import synapps.resona.api.socialMedia.comment.dto.request.CommentUpdateRequest;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.response.CommentDeleteResponse;
import synapps.resona.api.socialMedia.comment.service.CommentService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Comment", description = "댓글 및 답글 API")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
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
  @PostMapping
  public ResponseEntity<SuccessResponse<CommentDto>> registerComment(HttpServletRequest request,
      @Valid @RequestBody CommentRequest commentRequest) {
    CommentDto response = commentService.register(commentRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "특정 피드의 모든 댓글 조회", description = "특정 피드에 달린 모든 댓글과 답글을 계층 구조로 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_COMMENTS_SUCCESS", listElementClass = CommentDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}))
  @GetMapping("/all/{feedId}")
  public ResponseEntity<SuccessResponse<List<CommentDto>>> getComments(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "댓글을 조회할 피드의 ID", required = true) @PathVariable Long feedId) {
    List<CommentDto> response = commentService.getCommentsByFeedId(userPrincipal.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_COMMENTS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_COMMENTS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글 수정", description = "댓글의 내용을 수정합니다. (댓글 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "EDIT_COMMENT_SUCCESS", responseClass = CommentDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @PutMapping("/{commentId}")
  @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<CommentDto>> editComment(HttpServletRequest request,
      @Parameter(description = "수정할 댓글의 ID", required = true) @PathVariable Long commentId,
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
    commentUpdateRequest.setCommentId(commentId);
    CommentDto response = commentService.edit(commentUpdateRequest);
    return ResponseEntity
        .status(SocialSuccessCode.EDIT_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.EDIT_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글 단건 조회", description = "특정 댓글 하나의 정보를 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_COMMENT_SUCCESS", responseClass = CommentDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}))
  @GetMapping("/{commentId}")
  public ResponseEntity<SuccessResponse<CommentDto>> getComment(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "조회할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentDto response = commentService.getComment(userPrincipal.getMemberId(), commentId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글의 답글 목록 조회", description = "특정 댓글에 달린 모든 답글을 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_REPLIES_SUCCESS", listElementClass = ReplyDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}))
  @GetMapping("/{commentId}/replies")
  public ResponseEntity<SuccessResponse<List<ReplyDto>>> getReplies(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "답글을 조회할 댓글의 ID", required = true) @PathVariable Long commentId) {
    List<ReplyDto> response = commentService.getReplies(userPrincipal.getMemberId(), commentId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_REPLIES_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_REPLIES_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (댓글 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_COMMENT_SUCCESS", responseClass = CommentDeleteResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/{commentId}")
  @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<CommentDeleteResponse>> deleteComment(HttpServletRequest request,
      @Parameter(description = "삭제할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentDeleteResponse response = commentService.deleteComment(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }
}