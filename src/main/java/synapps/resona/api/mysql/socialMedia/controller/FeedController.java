package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.metadata.CursorBasedMetaDataDto;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedRegistrationRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedReadResponse;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.mysql.socialMedia.service.FeedService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {

  private final FeedService feedService;
  private final ServerInfoConfig serverInfo;

  private MetaDataDto createSuccessMetaData(String queryString) {
    return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  private MetaDataDto createSuccessMetaData(String queryString, String cursor, int size,
      boolean hasNext) {
    return CursorBasedMetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName(), cursor, size, hasNext);
  }

  @PostMapping("/feed")
  public ResponseEntity<?> registerFeed(HttpServletRequest request,
      @Valid @RequestBody FeedRegistrationRequest feedRegistrationRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    FeedResponse feedResponse = feedService.registerFeed(
        feedRegistrationRequest.getMetadataList(),
        feedRegistrationRequest.getFeedRequest()
    );

    ResponseDto responseData = new ResponseDto(metaData, List.of(feedResponse));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/feed/{feedId}")
  public ResponseEntity<?> readFeed(HttpServletRequest request,
      @PathVariable Long feedId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.readFeed(feedId)));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/feed/test/all")
  public ResponseEntity<?> readAllFeed(HttpServletRequest request) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.readAllFeeds()));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/feeds")
  public ResponseEntity<?> readFeedByCursor(HttpServletRequest request,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size) {
    CursorResult<FeedReadResponse> feeds = feedService.getFeedsByCursor(cursor, size);
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString(), feeds.getCursor(), size,
        feeds.isHasNext());
    ResponseDto responseData = new ResponseDto(metaData, List.of(feeds.getValues()));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/feeds/member/{memberId}")
  public ResponseEntity<?> readFeedsByMember(@PathVariable Long memberId,
      HttpServletRequest request) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(feedService.getFeedsWithMediaAndLikeCount(memberId)));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/feeds/member/{memberId}/cursor")
  public ResponseEntity<?> readFeedsByMemberWithCursor(HttpServletRequest request,
      @PathVariable Long memberId,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size) {
    CursorResult<FeedReadResponse> result = feedService.getFeedsByCursorAndMemberId(cursor, size,
        memberId);
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString(), result.getCursor(), size,
        result.isHasNext());
    ResponseDto responseData = new ResponseDto(metaData, List.of(result.getValues()));
    return ResponseEntity.ok(responseData);
  }


  @PutMapping("/feed/{feedId}")
  @PreAuthorize("@socialSecurity.isFeedMemberProperty(#feedId) or hasRole('ADMIN')")
  public ResponseEntity<?> editFeed(HttpServletRequest request,
      @PathVariable Long feedId,
      @Valid @RequestBody FeedUpdateRequest feedRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(feedService.updateFeed(feedId, feedRequest)));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping("/feed/{feedId}")
  @PreAuthorize("@socialSecurity.isFeedMemberProperty(#feedId) or hasRole('ADMIN')")
  public ResponseEntity<?> deleteFeed(HttpServletRequest request,
      @PathVariable Long feedId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.deleteFeed(feedId)));
    return ResponseEntity.ok(responseData);
  }

}
