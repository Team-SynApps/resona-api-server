package synapps.resona.api.mysql.social_media.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
import synapps.resona.api.mysql.social_media.dto.feed.FeedRequest;
import synapps.resona.api.mysql.social_media.dto.feed.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.service.FeedService;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    public ResponseEntity<?> registerFeed(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @Valid @RequestBody FeedRequest feedRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.register(feedRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping
    public ResponseEntity<?> readFeed(HttpServletRequest request,
                                              HttpServletResponse response,
                                      @RequestParam Long feedId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.readFeed(feedId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping
    public ResponseEntity<?> editFeed(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @Valid @RequestBody FeedUpdateRequest feedRequest) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.updateFeed(feedRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePersonalInfo(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestParam Long feedId) throws Exception {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(feedService.deleteFeed(feedId)));
        return ResponseEntity.ok(responseData);
    }

}
