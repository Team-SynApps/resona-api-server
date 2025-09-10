package synapps.resona.api.socialMedia.service.comment;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.socialMedia.dto.comment.ReplyDto;
import synapps.resona.api.socialMedia.dto.comment.ReplyProjectionDto;
import synapps.resona.api.socialMedia.entity.comment.ContentDisplayStatus;
import synapps.resona.api.socialMedia.entity.comment.Reply;

@Component
@RequiredArgsConstructor
public class ReplyProcessor {

  public List<ReplyDto> process(List<ReplyProjectionDto> projectionDtos) {
    return projectionDtos.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  private ReplyDto toDto(ReplyProjectionDto dto) {
    Reply reply = dto.getReply();
    ContentDisplayStatus status = determineStatus(reply, dto.isBlocked(), false);
    String content = determineContent(reply.getContent(), status);

    return ReplyDto.of(reply, status, content);
  }

  private ContentDisplayStatus determineStatus(BaseEntity entity, boolean isBlocked, boolean isHidden) {
    if (entity.isDeleted()) return ContentDisplayStatus.DELETED;
    if (isBlocked) return ContentDisplayStatus.BLOCKED;
    if (isHidden) return ContentDisplayStatus.HIDDEN;
    return ContentDisplayStatus.NORMAL;
  }

  private String determineContent(String originalContent, ContentDisplayStatus status) {
    return switch (status) {
      case BLOCKED, HIDDEN, DELETED -> status.toString();
      default -> originalContent;
    };
  }
}