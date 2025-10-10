package com.synapps.resona.comment.adapter.in.web;

import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.code.SocialSuccessCode;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.query.service.CommentRetrievalService;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.Language;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment Query", description = "댓글/대댓글 조회 API")
@RestController
@RequiredArgsConstructor
public class CommentQueryController {

  private final CommentRetrievalService commentRetrievalService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "특정 피드의 모든 댓글 조회", description = "특정 피드에 달린 모든 댓글과 답글을 계층 구조로 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_COMMENTS_SUCCESS", listElementClass = CommentDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}))
  @GetMapping("/feeds/{feedId}/comments")
  public ResponseEntity<SuccessResponse<Page<CommentDto>>> getComments(
      HttpServletRequest request,
      @Parameter(description = "언어 코드") @RequestParam(name = "lang", defaultValue = "en") String languageCode,
      @PathVariable Long feedId,
      @AuthenticationPrincipal AuthenticatedUser user,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    Language targetLanguage = Language.fromCode(languageCode);
    Page<CommentDto> comments = commentRetrievalService.getCommentsForFeed(feedId, user.getMemberId(), targetLanguage, pageable);
    return ResponseEntity
        .status(SocialSuccessCode.GET_COMMENTS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_COMMENTS_SUCCESS, createRequestInfo(request.getRequestURI()), comments));
  }
}