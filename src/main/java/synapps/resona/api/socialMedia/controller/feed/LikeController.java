package synapps.resona.api.socialMedia.controller.feed;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.dto.like.request.LikeRequest;
import synapps.resona.api.socialMedia.service.feed.LikeService;
import synapps.resona.api.socialMedia.dto.like.response.LikeResponse;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Like", description = "좋아요/좋아요 취소 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "좋아요 등록", description = "피드 또는 댓글에 좋아요를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_SUCCESS", responseClass = LikeResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
      // TODO: 서비스 로직에 중복 체크가 추가 필요 ('ALREADY_LIKED')
  })
  @PostMapping("/likes/{feedId}")
  public ResponseEntity<SuccessResponse<LikeResponse>> registerLike(HttpServletRequest request,
      @Parameter(description = "취소할 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    LikeResponse response = likeService.register(feedId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "좋아요 취소", description = "등록했던 좋아요를 취소합니다. (본인 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/likes/{feedId}")
//  @PreAuthorize("@socialSecurity.isLikeMemberProperty(#likeId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> cancelLike(HttpServletRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "취소할 좋아요의 피드 ID", required = true) @PathVariable Long feedId) {
    likeService.cancel(feedId, userPrincipal.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}