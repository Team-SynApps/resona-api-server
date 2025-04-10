package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.socialMedia.service.CommentService;

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
                                             @Valid @RequestBody CommentRequest commentRequest) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.register(commentRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(HttpServletRequest request,
                                        @PathVariable Long commentId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getComment(commentId)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/all/{feedId}")
    public ResponseEntity<?> getComments(HttpServletRequest request,
                                         @PathVariable Long feedId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getCommentsByFeedId(feedId)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(HttpServletRequest request,
                                        @PathVariable Long commentId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.getReplies(commentId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
    public ResponseEntity<?> editComment(HttpServletRequest request,
                                         @PathVariable Long commentId,
                                         @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentUpdateRequest.setCommentId(commentId);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.edit(commentUpdateRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("@socialSecurity.isCommentMemberProperty(#commentId) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(HttpServletRequest request,
                                           @PathVariable Long commentId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(commentService.deleteComment(commentId)));
        return ResponseEntity.ok(responseData);
    }
}