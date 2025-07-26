package synapps.resona.api.mysql.socialMedia.controller.mention;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.ErrorResponse;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.SuccessResponse;
import synapps.resona.api.mysql.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;
import synapps.resona.api.mysql.socialMedia.service.mention.MentionService;

@Tag(name = "Mention", description = "사용자 맨션 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MentionController {

  private final MentionService mentionService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "맨션 등록", description = "댓글/답글을 통해 사용자를 맨션합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "맨션 등록 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 댓글 또는 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/mention/{commentId}")
  public ResponseEntity<SuccessResponse<Mention>> registerMention(HttpServletRequest request,
      @Parameter(description = "맨션이 포함된 댓글의 ID", required = true) @PathVariable Long commentId) {
    Mention mention = mentionService.register(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_MENTION_SUCCESS, createRequestInfo(request.getQueryString()), mention));
  }

  @Operation(summary = "맨션 조회", description = "특정 맨션 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "맨션 조회 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 맨션",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/mention/{mentionId}")
  public ResponseEntity<SuccessResponse<Mention>> readMention(HttpServletRequest request,
      @Parameter(description = "조회할 맨션의 ID", required = true) @PathVariable Long mentionId) {
    Mention mention = mentionService.read(mentionId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_MENTION_SUCCESS, createRequestInfo(request.getQueryString()), mention));
  }

  @Operation(summary = "맨션 삭제", description = "맨션을 삭제합니다. (맨션한 사람 또는 관리자만 가능)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "맨션 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 맨션",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/mention/{mentionId}")
  @PreAuthorize("@socialSecurity.isMentionMemberProperty(#mentionId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteMention(HttpServletRequest request,
      @Parameter(description = "삭제할 맨션의 ID", required = true) @PathVariable Long mentionId) {
    mentionService.delete(mentionId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_MENTION_SUCCESS, createRequestInfo(request.getQueryString())));
  }
}