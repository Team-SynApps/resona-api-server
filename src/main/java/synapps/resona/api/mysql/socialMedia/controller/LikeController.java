package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.LikeRequest;
import synapps.resona.api.mysql.socialMedia.entity.Likes;
import synapps.resona.api.mysql.socialMedia.service.LikeService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping("/like")
    public ResponseEntity<?> registerLike(HttpServletRequest request,
                                          @RequestBody LikeRequest likeRequest) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Likes like = likeService.register(likeRequest);
        ResponseDto responseData = new ResponseDto(metaData, List.of(like));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/like/{likeId}")
    public ResponseEntity<?> cancelLike(HttpServletRequest request,
                                        @PathVariable Long likeId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Likes like = likeService.cancel(likeId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(like));
        return ResponseEntity.ok(responseData);
    }
}
