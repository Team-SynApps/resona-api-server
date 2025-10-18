package com.synapps.resona.feed.command.controller;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.feed.dto.ScrapResponse;
import com.synapps.resona.feed.command.service.ScrapCommandService;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feed", description = "피드 관련 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ScrapCommandController {

  private final ScrapCommandService scrapCommandService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 스크랩", description = "특정 피드를 스크랩합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "SCRAP_SUCCESS", responseClass = ScrapResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "SCRAP_ALREADY_EXIST"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/scrap/{feedId}")
  public ResponseEntity<SuccessResponse<ScrapResponse>> registerScrap(HttpServletRequest request,
      @Parameter(description = "스크랩할 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal AuthenticatedUser member) {
    ScrapResponse scrap = scrapCommandService.scrapFeed(member.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.SCRAP_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.SCRAP_SUCCESS, createRequestInfo(request.getRequestURI()), scrap));
  }

  @Operation(summary = "스크랩 취소", description = "스크랩했던 피드를 취소합니다. (본인 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "CANCEL_SCRAP_SUCCESS")) // responseClass 제거
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"SCRAP_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @DeleteMapping("/scrap/{feedId}")
  @PreAuthorize("@socialSecurity.isScrapMemberProperty(#scrapId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<Void>> cancelScrap(HttpServletRequest request,
      @AuthenticationPrincipal AuthenticatedUser member,
      @Parameter(description = "취소할 스크랩의 ID", required = true) @PathVariable Long feedId) {
    scrapCommandService.unscrapFeed(member.getMemberId(), feedId);
    return ResponseEntity
        .status(SocialSuccessCode.CANCEL_SCRAP_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.CANCEL_SCRAP_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}