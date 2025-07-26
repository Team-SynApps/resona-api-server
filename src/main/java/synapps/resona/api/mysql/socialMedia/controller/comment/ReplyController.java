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
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyPostResponse;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyReadResponse;
import synapps.resona.api.mysql.socialMedia.service.comment.ReplyService;

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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "답글 등록 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<SuccessResponse<ReplyPostResponse>> registerReply(HttpServletRequest request,
      @Valid @RequestBody ReplyRequest replyRequest) {
    ReplyPostResponse response = replyService.register(replyRequest);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_REPLY_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "단일 답글 조회", description = "특정 답글의 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "답글 조회 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 답글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{replyId}")
  public ResponseEntity<SuccessResponse<ReplyReadResponse>> getReply(HttpServletRequest request,
      @Parameter(description = "조회할 답글의 ID", required = true) @PathVariable Long replyId) {
    ReplyReadResponse response = replyService.read(replyId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_REPLY_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "답글 수정", description = "답글의 내용을 수정합니다. (답글 작성자 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "답글 수정 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 답글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<ReplyReadResponse>> updateReply(HttpServletRequest request,
      @Parameter(description = "수정할 답글의 ID", required = true) @PathVariable Long replyId,
      @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest) {
    replyUpdateRequest.setReplyId(replyId);
    ReplyReadResponse response = replyService.update(replyUpdateRequest);
    return ResponseEntity
        .status(SocialSuccessCode.EDIT_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.EDIT_REPLY_SUCCESS, createRequestInfo(request.getQueryString()), response));
  }

  @Operation(summary = "답글 삭제", description = "답글을 삭제합니다. (답글 작성자 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "답글 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 답글",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteReply(HttpServletRequest request,
      @Parameter(description = "삭제할 답글의 ID", required = true) @PathVariable Long replyId) {
    replyService.delete(replyId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_REPLY_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_REPLY_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}