package synapps.resona.api.mysql.socialMedia.controller.feed;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.LikeRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Likes;
import synapps.resona.api.mysql.socialMedia.service.feed.LikeService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping("/like")
  public ResponseEntity<?> registerLike(HttpServletRequest request,
      @RequestBody LikeRequest likeRequest) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    Likes like = likeService.register(likeRequest);
    ResponseDto responseData = new ResponseDto(metaData, List.of(like));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping("/like/{likeId}")
  @PreAuthorize("@socialSecurity.isLikeMemberProperty(#likeId) or hasRole('ADMIN')")
  public ResponseEntity<?> cancelLike(HttpServletRequest request,
      @PathVariable Long likeId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    Likes like = likeService.cancel(likeId);
    ResponseDto responseData = new ResponseDto(metaData, List.of(like));
    return ResponseEntity.ok(responseData);
  }
}
