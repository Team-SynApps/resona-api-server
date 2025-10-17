package com.synapps.resona.feed.command.controller;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.feed.command.service.FeedCommandLikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feed", description = "피드 관련 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedLikeCommandController {

  private final FeedCommandLikesService feedCommandLikesService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 좋아요", description = "특정 피드에 좋아요를 누릅니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "LIKE_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "LIKE_NOT_FOUND", "ALREADY_LIKED"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/likes/feed/{feedId}")
  public ResponseEntity<SuccessResponse<Void>> likeFeed(
      HttpServletRequest request,
      @Parameter(description = "좋아요를 누를 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal AuthenticatedUser user) {
    feedCommandLikesService.likeFeed(user.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.LIKE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.LIKE_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "피드 좋아요 취소", description = "눌렀던 좋아요를 취소합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "UNLIKE_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "LIKE_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @DeleteMapping("/likes/feed/{feedId}")
  public ResponseEntity<SuccessResponse<Void>> unlikeFeed(
      HttpServletRequest request,
      @Parameter(description = "좋아요를 취소할 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal AuthenticatedUser user) {
    feedCommandLikesService.unlikeFeed(user.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.UNLIKE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.UNLIKE_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}
