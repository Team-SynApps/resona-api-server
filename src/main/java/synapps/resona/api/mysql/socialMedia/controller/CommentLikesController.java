package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.mysql.socialMedia.service.CommentLikesService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentLikesController {
    private final CommentLikesService commentLikesService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
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
    public ResponseEntity<?> cancelCommentLike(HttpServletRequest request,
                                               @PathVariable Long commentLikeId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        CommentLikes commentLikes = commentLikesService.cancel(commentLikeId);
        ResponseDto response = new ResponseDto(metaData, List.of(commentLikes));
        return ResponseEntity.ok(response);
    }
}
