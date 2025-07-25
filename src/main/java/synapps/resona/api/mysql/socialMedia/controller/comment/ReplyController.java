package synapps.resona.api.mysql.socialMedia.controller.comment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.socialMedia.service.comment.ReplyService;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

  private final ReplyService replyService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping
  public ResponseEntity<?> registerReply(HttpServletRequest request,
      @Valid @RequestBody ReplyRequest replyRequest) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(replyService.register(replyRequest)));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/{replyId}")
  public ResponseEntity<?> getReply(HttpServletRequest request,
      @PathVariable Long replyId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.read(replyId)));
    return ResponseEntity.ok(responseData);
  }

  @PutMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<?> updateReply(HttpServletRequest request,
      @PathVariable Long replyId,
      @Valid @RequestBody ReplyUpdateRequest replyUpdateRequest) {
    replyUpdateRequest.setReplyId(replyId);
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData,
        List.of(replyService.update(replyUpdateRequest)));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping("/{replyId}")
  @PreAuthorize("@socialSecurity.isReplyMemberProperty(#replyId) or hasRole('ADMIN')")
  public ResponseEntity<?> deleteReply(HttpServletRequest request,
      @PathVariable Long replyId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    ResponseDto responseData = new ResponseDto(metaData, List.of(replyService.delete(replyId)));
    return ResponseEntity.ok(responseData);
  }
}