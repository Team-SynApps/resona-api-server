package synapps.resona.api.mysql.socialMedia.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.mysql.socialMedia.service.comment.CommentLikesService;

@Tag(name = "Comment Like", description = "댓글 좋아요 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentLikesController {

  private final CommentLikesService commentLikesService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "댓글 좋아요 등록", description = "특정 댓글에 좋아요를 등록합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "좋아요 처리 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "이미 좋아요를 누른 댓글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/comment-like")
  public ResponseEntity<SuccessResponse<CommentLikes>> registerCommentLike(HttpServletRequest request,
      @RequestBody CommentLikesRequest commentLikesRequest) {
    CommentLikes commentLikes = commentLikesService.register(commentLikesRequest);
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_COMMENT_SUCCESS, createRequestInfo(request.getQueryString()), commentLikes));
  }

  @Operation(summary = "댓글 좋아요 취소", description = "등록했던 댓글 좋아요를 취소합니다. (본인 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 좋아요",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/comment-like/{commentLikeId}")
  @PreAuthorize("@socialSecurity.isCommentLikesMemberProperty(#commentLikeId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> cancelCommentLike(HttpServletRequest request,
      @Parameter(description = "취소할 댓글 좋아요의 ID", required = true) @PathVariable Long commentLikeId) {
    commentLikesService.cancel(commentLikeId);
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}