package com.synapps.resona.query.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.domain.entity.comment.ContentDisplayStatus;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.query.dto.feed.SocialMemberDto;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyDto {

  private Long commentId;

  private Long replyId;

  private SocialMemberDto author;

  private ContentDisplayStatus status;

  private String content;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  public static ReplyDto of(Reply reply, ContentDisplayStatus status, String content) {
    return ReplyDto.builder()
        .commentId(reply.getComment().getId())
        .replyId(reply.getId())
        .author(SocialMemberDto.from(reply.getMember()))
        .status(status)
        .content(content)
        .createdAt(reply.getCreatedAt())
        .build();
  }
}
