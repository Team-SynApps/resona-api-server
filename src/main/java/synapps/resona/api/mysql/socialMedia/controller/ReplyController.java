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
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.socialMedia.service.ReplyService;

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
                                           @Valid @RequestBody ReplyRequest replyRequest){
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.register(replyRequest)));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<?> getReply(HttpServletRequest request,
                                      @PathVariable Long replyId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.read(replyId)));
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/{replyId}")
    @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
    public ResponseEntity<?> updateReply(HttpServletRequest request,
                                         @PathVariable Long replyId,
                                         @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest) {
        replyUpdateRequest.setReplyId(replyId);
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.update(replyUpdateRequest)));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/{replyId}")
    @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReply(HttpServletRequest request,
                                         @PathVariable Long replyId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.delete(replyId)));
        return ResponseEntity.ok(responseData);
    }
}