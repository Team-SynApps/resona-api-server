package synapps.resona.api.mysql.socialMedia.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentPostResponse;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentReadResponse;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentUpdateResponse;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyReadResponse;
import synapps.resona.api.mysql.socialMedia.service.comment.CommentService;

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

  @Operation(summary = "댓글/답글 등록", description = "피드에 새로운 댓글을 등록하거나, 다른 댓글에 대한 답글을 등록합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "댓글 등록 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<CommentPostResponse>> registerComment(HttpServletRequest request,
      @Valid @RequestBody CommentRequest commentRequest) {
    CommentPostResponse response = commentService.register(commentRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_COMMENT_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "특정 피드의 모든 댓글 조회", description = "특정 피드에 달린 모든 댓글과 답글을 계층 구조로 조회합니다.")
  @GetMapping("/all/{feedId}")
  public ResponseEntity<SuccessResponse<List<CommentPostResponse>>> getComments(HttpServletRequest request,
      @Parameter(description = "댓글을 조회할 피드의 ID", required = true) @PathVariable Long feedId) {
    List<CommentPostResponse> response = commentService.getCommentsByFeedId(feedId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_COMMENTS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_COMMENTS_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "댓글 수정", description = "댓글의 내용을 수정합니다. (댓글 작성자 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 댓글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/{commentId}")
  @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<CommentUpdateResponse>> editComment(HttpServletRequest request,
      @Parameter(description = "수정할 댓글의 ID", required = true) @PathVariable Long commentId,
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
    commentUpdateRequest.setCommentId(commentId);
    CommentUpdateResponse response = commentService.edit(commentUpdateRequest);
    return ResponseEntity
        .status(SocialSuccessCode.EDIT_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.EDIT_COMMENT_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "댓글 단건 조회", description = "특정 댓글 하나의 정보를 조회합니다.")
  @GetMapping("/{commentId}")
  public ResponseEntity<SuccessResponse<CommentReadResponse>> getComment(HttpServletRequest request,
      @Parameter(description = "조회할 댓글의 ID", required = true) @PathVariable Long commentId) {
    CommentReadResponse response = commentService.getComment(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_COMMENT_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "댓글의 답글 목록 조회", description = "특정 댓글에 달린 모든 답글을 조회합니다.")
  @GetMapping("/{commentId}/replies")
  public ResponseEntity<SuccessResponse<List<ReplyReadResponse>>> getReplies(HttpServletRequest request,
      @Parameter(description = "답글을 조회할 댓글의 ID", required = true) @PathVariable Long commentId) {
    List<ReplyReadResponse> response = commentService.getReplies(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_REPLIES_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_REPLIES_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (댓글 작성자 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 댓글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{commentId}")
  @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteComment(HttpServletRequest request,
      @Parameter(description = "삭제할 댓글의 ID", required = true) @PathVariable Long commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_COMMENT_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}