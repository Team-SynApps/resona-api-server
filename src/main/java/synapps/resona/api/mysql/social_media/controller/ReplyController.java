package synapps.resona.api.mysql.social_media.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.social_media.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.social_media.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.social_media.service.ReplyService;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.ReplyNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping
    public ResponseEntity<?> registerReply(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @Valid @RequestBody ReplyRequest replyRequest) throws CommentNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.register(replyRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<?> getReply(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @PathVariable Long replyId) throws ReplyNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.read(replyId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<?> updateReply(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable Long replyId,
                                         @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest) throws ReplyNotFoundException {
        replyUpdateRequest.setReplyId(replyId);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.update(replyUpdateRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable Long replyId) throws ReplyNotFoundException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.delete(replyId)));
        return ResponseEntity.ok(responseData);
    }
}