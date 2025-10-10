package com.synapps.resona.feed.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.feed.command.service.FeedCommandHideService;
import com.synapps.resona.feed.dto.FeedCreateDto;
import com.synapps.resona.feed.command.service.FeedCommandService;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.feed.dto.request.FeedRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

@Tag(name = "Feed", description = "피드 관련 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedCommandController {

  private final FeedCommandService feedCommandService;
  private final FeedCommandHideService feedCommandHideService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 등록", description = "새로운 피드를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_FEED_SUCCESS", responseClass = FeedCreateDto.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed")
  public ResponseEntity<SuccessResponse<FeedCreateDto>> registerFeed(HttpServletRequest request,
      @Valid @RequestBody FeedRegistrationRequest feedRegistrationRequest) {
    FeedCreateDto feedResponse = feedCommandService.registerFeed(
        feedRegistrationRequest.getMetadataList(),
        feedRegistrationRequest.getFeedRequest()
    );

    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), feedResponse));
  }


  @Operation(summary = "피드 삭제", description = "특정 피드를 삭제합니다. (피드 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "DELETE_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/feed/{feedId}")
  @PreAuthorize("@socialSecurity.isFeedMemberProperty(#feedId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> deleteFeed(HttpServletRequest request,
      @Parameter(description = "삭제할 피드의 ID", required = true) @PathVariable Long feedId) {
    feedCommandService.deleteFeed(feedId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

  @Operation(summary = "피드 숨기기", description = "특정 피드를 현재 사용자의 피드에서 숨깁니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "HIDE_FEED_SUCCESS"))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed/{feedId}/hide")
  public ResponseEntity<SuccessResponse<Void>> hideFeed(
      HttpServletRequest request,
      @Parameter(description = "숨길 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal AuthenticatedUser user) {
    feedCommandHideService.hideFeed(user.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.HIDE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.HIDE_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }

}