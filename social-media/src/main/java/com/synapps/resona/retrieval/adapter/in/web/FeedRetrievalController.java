package com.synapps.resona.retrieval.adapter.in.web;

import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.retrieval.config.FeedRetrievalProperties;
import com.synapps.resona.retrieval.dto.FeedDetailDto;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.service.FeedRetrievalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedRetrievalController {

    private final FeedRetrievalService feedRetrievalService;
    private final ServerInfoConfig serverInfo;
    private final FeedRetrievalProperties properties;

    private RequestInfo createRequestInfo(String path) {
        return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
    }

    @Operation(summary = "홈 피드 조회 (커서 기반 페이지네이션)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEEDS_SUCCESS"))
    @GetMapping("/feeds")
    public ResponseEntity<SuccessResponse<CursorResult<FeedDto>>> getHomeFeeds(
        HttpServletRequest request,
        @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
        @Parameter(description = "다음 페이지 커서") @RequestParam(required = false) String cursor,
        @Parameter(description = "페이지 크기") @RequestParam(required = false) Integer size,
        @Parameter(description = "필터링할 카테고리 (예: DAILY, TRAVEL)") @RequestParam(required = false) String category,
        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
    ) {
        Language targetLanguage = Language.fromCode(languageCode);
        FeedCategory feedCategory = (category != null) ? FeedCategory.of(category) : null;
        int pageSize = (size != null) ? size : properties.defaultPageSize();

        CursorResult<FeedDto> result = feedRetrievalService.getHomeFeeds(
            user.getMemberId(), targetLanguage, cursor, pageSize, feedCategory
        );

        return ResponseEntity.status(SocialSuccessCode.GET_FEEDS_SUCCESS.getStatus())
            .body(SuccessResponse.of(SocialSuccessCode.GET_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), result));
    }

    @Operation(summary = "탐색 피드 조회 (국가/카테고리별 최신 피드)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEEDS_SUCCESS"))
    @GetMapping("/feeds/explore")
    public ResponseEntity<SuccessResponse<CursorResult<FeedDto>>> getExploreFeeds(
        HttpServletRequest request,
        @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
        @Parameter(description = "다음 페이지 커서") @RequestParam(required = false) String cursor,
        @Parameter(description = "페이지 크기") @RequestParam(required = false) Integer size,
        @Parameter(description = "필터링할 작성자 거주 국가 코드 (예: KR, US)") @RequestParam(required = false) String country,
        @Parameter(description = "필터링할 카테고리 (예: DAILY, TRAVEL)") @RequestParam(required = false) String category,
        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
    ) {
        Language targetLanguage = Language.fromCode(languageCode);
        CountryCode countryCode = (country != null) ? CountryCode.valueOf(country) : null;
        FeedCategory feedCategory = (category != null) ? FeedCategory.of(category) : null;
        int pageSize = (size != null) ? size : properties.defaultPageSize();

        CursorResult<FeedDto> result = feedRetrievalService.getExploreFeeds(
            user.getMemberId(), targetLanguage, cursor, pageSize, countryCode, feedCategory
        );

        return ResponseEntity.status(SocialSuccessCode.GET_FEEDS_SUCCESS.getStatus())
            .body(SuccessResponse.of(SocialSuccessCode.GET_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), result));
    }

    @Operation(summary = "내 피드 목록 조회 (오프셋 기반 페이지네이션)")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEEDS_SUCCESS"))
    @GetMapping("/feeds/me")
    public ResponseEntity<SuccessResponse<Page<FeedDto>>> getMyFeeds(
        HttpServletRequest request,
        @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Language targetLanguage = Language.fromCode(languageCode);
        Page<FeedDto> myFeeds = feedRetrievalService.getMyFeeds(user.getMemberId(), targetLanguage, pageable);

        return ResponseEntity.status(SocialSuccessCode.GET_FEEDS_SUCCESS.getStatus())
            .body(SuccessResponse.of(SocialSuccessCode.GET_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), myFeeds));
    }

    @Operation(summary = "내 스크랩 목록 조회")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_SCRAPS_SUCCESS"))
    @GetMapping("/scraps/me")
    public ResponseEntity<SuccessResponse<Page<FeedDto>>> getMyScraps(
        HttpServletRequest request,
        @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Language targetLanguage = Language.fromCode(languageCode);
        Page<FeedDto> scrappedFeeds = feedRetrievalService.getMyScrappedFeeds(user.getMemberId(), targetLanguage, pageable);

        return ResponseEntity.ok(SuccessResponse.of(
            SocialSuccessCode.GET_SCRAPS_SUCCESS, createRequestInfo(request.getRequestURI()), scrappedFeeds
        ));
    }

    @Operation(summary = "단일 피드 상세 조회")
    @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEED_SUCCESS"))
    @GetMapping("/feeds/{feedId}")
    public ResponseEntity<SuccessResponse<FeedDetailDto>> getFeedDetail(
        HttpServletRequest request,
        @Parameter(description = "조회할 피드의 ID", required = true) @PathVariable Long feedId,
        @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Language targetLanguage = Language.fromCode(languageCode);

        FeedDetailDto feedDetail = feedRetrievalService.getFeedDetail(feedId, user.getMemberId(), targetLanguage, pageable);

        return ResponseEntity.ok(SuccessResponse.of(
            SocialSuccessCode.GET_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), feedDetail
        ));
    }
}