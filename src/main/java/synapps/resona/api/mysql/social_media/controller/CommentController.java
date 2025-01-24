package synapps.resona.api.mysql.social_media.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.social_media.service.CommentService;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    public ResponseEntity<?> registerComment(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @Valid @RequestBody CommentRequest commentRequest) throws FeedNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.register(commentRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable Long commentId) throws CommentNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getComment(commentId)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/all/{feedId}")
    public ResponseEntity<?> getComments(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable Long feedId) throws FeedNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getCommentsByFeedId(feedId)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable Long commentId) throws CommentNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getReplies(commentId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> editComment(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable Long commentId,
                                         @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) throws CommentNotFoundException {
        commentUpdateRequest.setCommentId(commentId);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.edit(commentUpdateRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable Long commentId) throws CommentNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.deleteComment(commentId)));
        return ResponseEntity.ok(responseData);
    }
}