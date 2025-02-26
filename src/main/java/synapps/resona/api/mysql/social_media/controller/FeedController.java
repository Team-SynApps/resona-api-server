package synapps.resona.api.mysql.social_media.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.dto.metadata.CursorBasedMetaDataDto;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedRegistrationRequest;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.feed.response.FeedResponse;
import synapps.resona.api.mysql.social_media.dto.feed.response.FeedReadResponse;
import synapps.resona.api.mysql.social_media.service.FeedService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    private MetaDataDto createSuccessMetaData(String queryString, String cursor, int size, boolean hasNext) {
        return CursorBasedMetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName(), cursor, size, hasNext);
    }

    @PostMapping("/feed")
    @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
    public ResponseEntity<?> registerFeed(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @Valid @RequestBody FeedRegistrationRequest feedRegistrationRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        FeedResponse feedResponse = feedService.registerFeed(
                feedRegistrationRequest.getMetadataList(),
                feedRegistrationRequest.getFeedRequest()
        );

        ResponseDto responseData = new ResponseDto(metaData, List.of(feedResponse));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/feed")
    public ResponseEntity<?> readFeed(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestParam Long feedId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.readFeed(feedId)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/feed/test/all")
    public ResponseEntity<?> readAllFeed(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.readAllFeeds()));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> readFeedByCursor(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam(required = false) String cursor,
                                              @RequestParam(defaultValue = "10") int size) throws Exception {
        CursorResult<FeedReadResponse> feeds = feedService.getFeedsByCursor(cursor, size);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString(), feeds.getCursor(), size, feeds.isHasNext());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feeds.getValues()));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/feed")
    @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
    public ResponseEntity<?> editFeed(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @Valid @RequestBody FeedUpdateRequest feedRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.updateFeed(feedRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/feed")
    @PreAuthorize("@memberSecurity.isCurrentUser(#request) or hasRole('ADMIN')")
    public ResponseEntity<?> deletePersonalInfo(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestParam Long feedId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.deleteFeed(feedId)));
        return ResponseEntity.ok(responseData);
    }

}
