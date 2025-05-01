package synapps.resona.api.mysql.socialMedia.controller;

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
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.mysql.socialMedia.service.CommentLikesService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentLikesController {

  private final CommentLikesService commentLikesService;
  private final ServerInfoConfig serverInfo;

  private MetaDataDto createSuccessMetaData(String queryString) {
    return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping("/comment-like")
  public ResponseEntity<?> registerCommentLike(HttpServletRequest request,
      @RequestBody CommentLikesRequest commentLikesRequest) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    CommentLikes commentLikes = commentLikesService.register(commentLikesRequest);
    ResponseDto response = new ResponseDto(metaData, List.of(commentLikes));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/comment-like/{commentLikeId}")
  @PreAuthorize("@socialSecurity.isCommentLikesMemberProperty(#commentLikeId) or hasRole('ADMIN')")
  public ResponseEntity<?> cancelCommentLike(HttpServletRequest request,
      @PathVariable Long commentLikeId) {
    MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
    CommentLikes commentLikes = commentLikesService.cancel(commentLikeId);
    ResponseDto response = new ResponseDto(metaData, List.of(commentLikes));
    return ResponseEntity.ok(response);
  }
}
