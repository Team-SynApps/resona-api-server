package com.synapps.resona.comment.service;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.CommentProjectionDto;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.dto.ReplyProjectionDto;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.entity.ContentDisplayStatus;
import com.synapps.resona.comment.entity.Reply;
import com.synapps.resona.entity.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

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