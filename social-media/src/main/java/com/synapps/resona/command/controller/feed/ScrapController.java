package com.synapps.resona.command.controller.feed;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.member.UserPrincipal;
import com.synapps.resona.query.dto.feed.ScrapReadResponse;
import com.synapps.resona.query.service.ScrapService;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.CursorResult;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scrap", description = "피드 스크랩 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ScrapController {

  private final ScrapService scrapService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 스크랩", description = "특정 피드를 스크랩합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "SCRAP_SUCCESS", responseClass = ScrapReadResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND", "SCRAP_ALREADY_EXIST"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/scrap/{feedId}")
  public ResponseEntity<SuccessResponse<ScrapReadResponse>> registerScrap(HttpServletRequest request,
      @Parameter(description = "스크랩할 피드의 ID", required = true) @PathVariable Long feedId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    ScrapReadResponse scrap = scrapService.register(feedId, MemberDto.from(userPrincipal));
    return ResponseEntity
        .status(SocialSuccessCode.SCRAP_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.SCRAP_SUCCESS, createRequestInfo(request.getRequestURI()), scrap));
  }

  @Operation(summary = "스크랩 단건 조회", description = "스크랩 ID로 특정 스크랩 정보를 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_SCRAP_SUCCESS", responseClass = ScrapReadResponse.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"SCRAP_NOT_FOUND"}))
  @GetMapping("/scrap/{scrapId}")
  public ResponseEntity<SuccessResponse<ScrapReadResponse>> readScrap(
      @PathVariable Long scrapId,
      HttpServletRequest request) {
    ScrapReadResponse responseDto = scrapService.read(scrapId);
    return ResponseEntity
        .ok(SuccessResponse.of(
            SocialSuccessCode.GET_SCRAP_SUCCESS,
            createRequestInfo(request.getRequestURI()),
            responseDto
        ));
  }

  @Operation(summary = "내 스크랩 목록 조회", description = "현재 로그인된 사용자의 스크랩 목록을 조회합니다. (커서 기반, 인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_SCRAPS_SUCCESS", cursor = true, listElementClass = ScrapReadResponse.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"}))
  @GetMapping("/scraps")
  public ResponseEntity<SuccessResponse<CursorResult<ScrapReadResponse>>> readScraps(HttpServletRequest request,
      @Parameter(description = "다음 페이지를 위한 커서 (첫 페이지는 비워둠)") @RequestParam(required = false) String cursor,
      @Parameter(description = "페이지 당 스크랩 수") @RequestParam(required = false, defaultValue = "10") int size,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    CursorResult<ScrapReadResponse> result = scrapService.readScrapsByCursor(cursor, size, MemberDto.from(userPrincipal));
    return ResponseEntity
        .status(SocialSuccessCode.GET_SCRAPS_SUCCESS.getStatus())
        .body(SuccessResponse.of(
            SocialSuccessCode.GET_SCRAPS_SUCCESS,
            createRequestInfo(request.getRequestURI()),
            result, result.getCursor(), size, result.isHasNext()));
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @Parameter(description = "취소할 스크랩의 ID", required = true) @PathVariable Long feedId) {
    scrapService.cancelScrap(feedId, user.getMemberId());
    return ResponseEntity
        .status(SocialSuccessCode.CANCEL_SCRAP_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.CANCEL_SCRAP_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}