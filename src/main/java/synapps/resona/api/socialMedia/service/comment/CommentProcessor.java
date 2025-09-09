package synapps.resona.api.socialMedia.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.socialMedia.dto.comment.CommentDto;
import synapps.resona.api.socialMedia.dto.comment.CommentProjectionDto;
import synapps.resona.api.socialMedia.dto.comment.ReplyProjectionDto;
import synapps.resona.api.socialMedia.dto.feed.SocialMemberDto;
import synapps.resona.api.socialMedia.dto.comment.ReplyDto;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.comment.ContentDisplayStatus;
import synapps.resona.api.socialMedia.entity.comment.Reply;

@Component
@RequiredArgsConstructor
public class CommentProcessor {

  public List<CommentDto> process(List<CommentProjectionDto> projectionDtos) {
    return projectionDtos.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public CommentDto processSingle(CommentProjectionDto projectionDto) {
    return toDto(projectionDto);
  }

  private CommentDto toDto(CommentProjectionDto dto) {
    Comment comment = dto.getComment();
    ContentDisplayStatus status = determineStatus(comment, dto.isBlocked(), dto.isHidden());
    String content = determineContent(comment.getContent(), status);

    List<ReplyDto> processedReplies = dto.getReplies().stream()
        .map(this::toReplyDto)
        .collect(Collectors.toList());

    return CommentDto.of(comment, status, content, dto.getLikeCount(), processedReplies);
  }

  private ReplyDto toReplyDto(ReplyProjectionDto replyDto) {
    Reply reply = replyDto.getReply();

    ContentDisplayStatus status = determineStatus(reply, replyDto.isBlocked(), replyDto.isHidden());
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