package synapps.resona.api.mysql.socialMedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.socialMedia.entity.Mention;
import synapps.resona.api.mysql.socialMedia.service.MentionService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MentionController {
    private final MentionService mentionService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping("/mention/{commentId}")
    public ResponseEntity<?> registerMention(HttpServletRequest request,
                                             @PathVariable Long commentId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Mention mention = mentionService.register(commentId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/mention/{mentionId}")
    public ResponseEntity<?> readMention(HttpServletRequest request,
                                         @PathVariable Long mentionId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Mention mention = mentionService.read(mentionId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/mention/{mentionId}")
    @PreAuthorize("@socialSecurity.isMentionMemberProperty(#mentionId) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteMention(HttpServletRequest request,
                                           @PathVariable Long mentionId) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        Mention mention = mentionService.delete(mentionId);
        ResponseDto responseData = new ResponseDto(metaData, List.of(mention));
        return ResponseEntity.ok(responseData);
    }
}
