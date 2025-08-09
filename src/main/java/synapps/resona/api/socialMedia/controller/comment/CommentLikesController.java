package synapps.resona.api.socialMedia.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import synapps.resona.api.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.socialMedia.dto.comment.response.CommentLikeResponse; // DTO 임포트
import synapps.resona.api.socialMedia.service.comment.CommentLikesService;

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
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_COMMENT_SUCCESS", responseClass = CommentLikeResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/comment-like")
  public ResponseEntity<SuccessResponse<CommentLikeResponse>> registerCommentLike(HttpServletRequest request,
      @RequestBody CommentLikesRequest commentLikesRequest) {
    CommentLikeResponse commentLikes = commentLikesService.register(commentLikesRequest);
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI()), commentLikes));
  }

  @Operation(summary = "댓글 좋아요 취소", description = "등록했던 댓글 좋아요를 취소합니다. (본인 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_COMMENT_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/comment-like/{commentLikeId}")
  @PreAuthorize("@socialSecurity.isCommentLikesMemberProperty(#commentLikeId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> cancelCommentLike(HttpServletRequest request,
      @Parameter(description = "취소할 댓글 좋아요의 ID", required = true) @PathVariable Long commentLikeId) {
    commentLikesService.cancel(commentLikeId);
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}