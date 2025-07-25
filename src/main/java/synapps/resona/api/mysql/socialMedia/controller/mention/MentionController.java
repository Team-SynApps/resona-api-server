package synapps.resona.api.mysql.socialMedia.controller.mention;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.Meta;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;
import synapps.resona.api.mysql.socialMedia.service.mention.MentionService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MentionController {

  private final MentionService mentionService;
  private final ServerInfoConfig serverInfo;

  private Meta createSuccessMetaData(String queryString) {
    return Meta.createSuccessMetaData(queryString, serverInfo.getApiVersion(),
        serverInfo.getServerName());
  }

  @PostMapping("/mention/{commentId}")
  public ResponseEntity<?> registerMention(HttpServletRequest request,
      @PathVariable Long commentId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    Mention mention = mentionService.register(commentId);
    ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/mention/{mentionId}")
  public ResponseEntity<?> readMention(HttpServletRequest request,
      @PathVariable Long mentionId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    Mention mention = mentionService.read(mentionId);
    ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
    return ResponseEntity.ok(responseData);
  }

  @DeleteMapping("/mention/{mentionId}")
  @PreAuthorize("@socialSecurity.isMentionMemberProperty(#mentionId) or hasRole('ADMIN')")
  public ResponseEntity<?> deleteMention(HttpServletRequest request,
      @PathVariable Long mentionId) {
    Meta metaData = createSuccessMetaData(request.getQueryString());
    Mention mention = mentionService.delete(mentionId);
    ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
    return ResponseEntity.ok(responseData);
  }
}
