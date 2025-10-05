package com.synapps.resona.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.common.entity.Author;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyDto {

  private Long replyId;
  private Author author;
  private String content;
  private long likeCount;
  private CommentDisplayStatus status;
  private List<MentionedMember> mentionedMembers;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  /**
   * ReplyEmbed 객체와 계산된 상태값을 기반으로 최종 ReplyDto를 생성
   * @param embed MongoDB의 내장 대댓글 객체
   * @param status 동적으로 계산된 최종 표시 상태
   * @param displayContent 상태에 따라 가공된 최종 표시 내용
   * @return 생성된 ReplyDto 객체
   */
  public static ReplyDto from(ReplyEmbed embed, CommentDisplayStatus status, String displayContent) {
    // 상태가 NORMAL이 아니면 개인정보 보호를 위해 작성자 정보를 null 처리
    Author displayAuthor = (status == CommentDisplayStatus.NORMAL) ? embed.getAuthor() : null;
    List<MentionedMember> displayMentions = (status == CommentDisplayStatus.NORMAL) ? embed.getMentionedMembers() : Collections.emptyList();

    return ReplyDto.builder()
        .replyId(embed.getReplyId())
        .author(displayAuthor)
        .content(displayContent)
        .likeCount(embed.getLikeCount())
        .status(status)
        .mentionedMembers(displayMentions)
        .createdAt(embed.getCreatedAt())
        .modifiedAt(embed.getModifiedAt())
        .build();
  }
}