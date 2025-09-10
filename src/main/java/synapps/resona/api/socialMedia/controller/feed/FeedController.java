package synapps.resona.api.socialMedia.controller.feed;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.member.code.AuthErrorCode;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.code.SocialErrorCode;
import synapps.resona.api.socialMedia.code.SocialSuccessCode;
import synapps.resona.api.socialMedia.dto.feed.request.FeedRegistrationRequest;
import synapps.resona.api.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.socialMedia.dto.feed.FeedDto;
import synapps.resona.api.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.socialMedia.service.feed.FeedService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Feed", description = "피드 관련 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {

  private final FeedService feedService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "피드 등록", description = "새로운 피드를 등록합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "REGISTER_FEED_SUCCESS", responseClass = FeedResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN"})
  })
  @PostMapping("/feed")
  public ResponseEntity<SuccessResponse<FeedResponse>> registerFeed(HttpServletRequest request,
      @Valid @RequestBody FeedRegistrationRequest feedRegistrationRequest) {
    FeedResponse feedResponse = feedService.registerFeed(
        feedRegistrationRequest.getMetadataList(),
        feedRegistrationRequest.getFeedRequest()
    );
    return ResponseEntity
        .status(SocialSuccessCode.REGISTER_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.REGISTER_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), feedResponse));
  }

  @Operation(summary = "단일 피드 조회", description = "특정 ID의 피드 하나를 상세 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEED_SUCCESS", responseClass = FeedDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}))
  @GetMapping("/feed/{feedId}")
  public ResponseEntity<SuccessResponse<FeedDto>> readFeed(HttpServletRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "조회할 피드의 ID", required = true) @PathVariable Long feedId) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    FeedDto response = feedService.readFeed(feedId, memberInfo.getId());
    return ResponseEntity
        .status(SocialSuccessCode.GET_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), response));
  }

  @Operation(summary = "전체 피드 목록 조회 (커서 기반)", description = "전체 피드 목록을 커서 기반 페이징으로 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_FEEDS_SUCCESS", cursor = true, listElementClass = FeedDto.class))
  @GetMapping("/feeds")
  public ResponseEntity<SuccessResponse<CursorResult<FeedDto>>> readFeedByCursor(HttpServletRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "다음 페이지를 위한 커서 (첫 페이지는 비워둠)") @RequestParam(required = false) String cursor,
      @Parameter(description = "페이지 당 피드 수") @RequestParam(defaultValue = "10") int size) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    Long viewerId = memberInfo.getId();
    CursorResult<FeedDto> feeds = feedService.getFeedsByCursor(viewerId, cursor, size);

    return ResponseEntity.status(SocialSuccessCode.GET_FEEDS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), feeds, feeds.getCursor(), size, feeds.isHasNext()));
  }

//  @Operation(summary = "특정 사용자 피드 목록 조회", description = "특정 사용자의 모든 피드 목록을 조회합니다.")
//  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_MEMBER_FEEDS_SUCCESS", listElementClass = FeedWithMediaDto.class))
//  @ApiErrorSpec(@ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}))
//  @GetMapping("/feeds/member/{memberId}")
//  public ResponseEntity<SuccessResponse<List<FeedWithMediaDto>>> readFeedsByMember(HttpServletRequest request,
//      @Parameter(description = "피드 목록을 조회할 사용자의 ID", required = true) @PathVariable Long memberId) {
//    List<FeedWithMediaDto> response = feedService.getFeedsWithMediaAndLikeCount(memberId);
//    return ResponseEntity
//        .status(SocialSuccessCode.GET_MEMBER_FEEDS_SUCCESS.getStatus())
//        .body(SuccessResponse.of(SocialSuccessCode.GET_MEMBER_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), response));
//  }

  @Operation(summary = "특정 사용자 피드 목록 조회 (커서 기반)", description = "특정 사용자의 피드 목록을 커서 기반 페이징으로 조회합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "GET_MEMBER_FEEDS_SUCCESS", cursor = true, listElementClass = FeedDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"}))
  @GetMapping("/feeds/member/{targetMemberId}/cursor")
  public ResponseEntity<SuccessResponse<CursorResult<FeedDto>>> readFeedsByMemberWithCursor(HttpServletRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Parameter(description = "피드 목록을 조회할 사용자의 ID", required = true) @PathVariable Long targetMemberId,
      @Parameter(description = "다음 페이지를 위한 커서 (첫 페이지는 비워둠)") @RequestParam(required = false) String cursor,
      @Parameter(description = "페이지 당 피드 수") @RequestParam(defaultValue = "10") int size) {
    MemberDto memberInfo = MemberDto.from(userPrincipal);
    Long viewerId = memberInfo.getId();
    CursorResult<FeedDto> result = feedService.getFeedsByCursorAndMemberId(viewerId, targetMemberId, cursor, size);

    return ResponseEntity.status(SocialSuccessCode.GET_MEMBER_FEEDS_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.GET_MEMBER_FEEDS_SUCCESS, createRequestInfo(request.getRequestURI()), result));
  }

  @Operation(summary = "피드 수정", description = "특정 피드의 내용을 수정합니다. (피드 작성자 또는 관리자만 가능)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = SocialSuccessCode.class, code = "EDIT_FEED_SUCCESS", responseClass = FeedResponse.class))
  @ApiErrorSpec({
      @ErrorCodeSpec(enumClass = SocialErrorCode.class, codes = {"FEED_NOT_FOUND"}),
      @ErrorCodeSpec(enumClass = AuthErrorCode.class, codes = {"TOKEN_NOT_FOUND", "INVALID_TOKEN", "FORBIDDEN"})
  })
  @PutMapping("/feed/{feedId}")
  @PreAuthorize("@socialSecurity.isFeedMemberProperty(#feedId) or hasRole('ADMIN')")
  public ResponseEntity<SuccessResponse<FeedResponse>> editFeed(HttpServletRequest request,
      @Parameter(description = "수정할 피드의 ID", required = true) @PathVariable Long feedId,
      @Valid @RequestBody FeedUpdateRequest feedRequest) {
    FeedResponse response = feedService.updateFeed(feedId, feedRequest);
    return ResponseEntity
        .status(SocialSuccessCode.EDIT_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.EDIT_FEED_SUCCESS, createRequestInfo(request.getRequestURI()), response));
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
    feedService.deleteFeed(feedId);
    return ResponseEntity
        .status(SocialSuccessCode.DELETE_FEED_SUCCESS.getStatus())
        .body(SuccessResponse.of(SocialSuccessCode.DELETE_FEED_SUCCESS, createRequestInfo(request.getRequestURI())));
  }
}