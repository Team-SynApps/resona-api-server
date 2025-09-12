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
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyRequest;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyUpdateRequest;
import synapps.resona.api.socialMedia.comment.service.ReplyService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Reply", description = "답글(대댓글) API")
@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

  private final ReplyService replyService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "답글 등록", description = "특정 댓글에 대한 답글을 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_REPLY_SUCCESS", responseClass = ReplyDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<ReplyDto>> registerReply(HttpServletRequest request,
      @Valid @RequestBody ReplyRequest replyRequest,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    ReplyDto response = replyService.register(replyRequest, MemberDto.from(userPrincipal));
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "답글 조회", description = "댓글의 답글들의 정보를 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_REPLY_SUCCESS", listElementClass = ReplyDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}))
  @GetMapping("/{commentId}")
  public ResponseEntity<SuccessResponse<List<ReplyDto>>> getReply(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "조회할 댓글의 ID", required = true) @PathVariable Long commentId) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    List<ReplyDto> response = replyService.readAll(memberInfo.getId(), commentId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "답글 수정", description = "답글의 내용을 수정합니다. (답글 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "EDIT_REPLY_SUCCESS", responseClass = ReplyDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @PutMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<ReplyDto>> updateReply(HttpServletRequest request,
      @Parameter(description = "수정할 답글의 ID", required = true) @PathVariable Long replyId,
      @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest) {
    replyUpdateRequest.setReplyId(replyId);
    ReplyDto response = replyService.update(replyUpdateRequest);
    return ResponseEntity
        .status(SocialSuccessCode.EDIT_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.EDIT_REPLY_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "답글 삭제", description = "답글을 삭제합니다. (답글 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_REPLY_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"REPLY_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteReply(HttpServletRequest request,
      @Parameter(description = "삭제할 답글의 ID", required = true) @PathVariable Long replyId) {
    replyService.delete(replyId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_REPLY_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}