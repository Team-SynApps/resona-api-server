package synapps.resona.api.socialMedia.controller.mention;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.dto.mention.MentionResponse; // 추가
import synapps.resona.api.socialMedia.service.mention.MentionService;

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
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_MENTION_SUCCESS", responseClass = MentionResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"COMMENT_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/mention/{commentId}")
  public ResponseEntity<SuccessResponse<MentionResponse>> registerMention(HttpServletRequest request,
      @Parameter(description = "맨션이 포함된 댓글의 ID", required = true) @PathVariable Long commentId) {
    MentionResponse mention = mentionService.register(commentId);
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_MENTION_SUCCESS, createRequestInfo(request.getRequestURI()), mention));
  }

  @Operation(summary = "맨션 조회", description = "특정 맨션 정보를 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_MENTION_SUCCESS", responseClass = MentionResponse.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"MENTION_NOT_FOUND"}))
  @GetMapping("/mention/{mentionId}")
  public ResponseEntity<SuccessResponse<MentionResponse>> readMention(HttpServletRequest request,
      @Parameter(description = "조회할 맨션의 ID", required = true) @PathVariable Long mentionId) {
    MentionResponse mention = mentionService.read(mentionId);
    return ResponseEntity
        .status(SocialSuccessCode.GET_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_MENTION_SUCCESS, createRequestInfo(request.getRequestURI()), mention));
  }

  @Operation(summary = "맨션 삭제", description = "맨션을 삭제합니다. (맨션한 사람 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_MENTION_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"MENTION_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/mention/{mentionId}")
  @PreAuthorize("@socialSecurity.isMentionMemberProperty(#mentionId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteMention(HttpServletRequest request,
      @Parameter(description = "삭제할 맨션의 ID", required = true) @PathVariable Long mentionId) {
    mentionService.delete(mentionId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_MENTION_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_MENTION_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}