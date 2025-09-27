package com.synapps.resona.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.entity.ContentDisplayStatus;
import com.synapps.resona.feed.dto.SocialMemberDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {
  private Long commentId;

  private SocialMemberDto author;

  private String content;

  private long likeCount;

  private ContentDisplayStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  private List<ReplyDto> replies = new ArrayList<>();

  public static CommentDto of(Comment comment, ContentDisplayStatus status, String content, long likeCount, List<ReplyDto> processedReplies) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(SocialMemberDto.from(comment.getMember()))
        .likeCount(likeCount)
        .content(content)
        .status(status)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .replies(processedReplies)
        .build();
  }

  // reply 없음
  public static CommentDto of(Comment comment) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(SocialMemberDto.from(comment.getMember()))
        .content(comment.getContent())
        .status(ContentDisplayStatus.NORMAL)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .build();
  }
}
