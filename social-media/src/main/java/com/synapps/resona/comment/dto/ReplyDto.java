package com.synapps.resona.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.profile.Profile;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyDto {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long parentCommentId;
  private Long replyId;
  private Author author;
  private String content;
  private long likeCount;
  private CommentDisplayStatus status;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MentionedMember> mentionedMembers;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String translatedContent;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  /**
   * ReplyEmbed Document 객체와 계산된 상태값을 기반으로 ReplyDto 생성 (query)
   * @param embed MongoDB의 내장 대댓글 객체
   * @param status 동적으로 계산된 최종 표시 상태
   * @param displayContent 상태에 따라 가공된 최종 표시 내용
   * @return 생성된 ReplyDto 객체
   */
  public static ReplyDto from(ReplyEmbed embed, CommentDisplayStatus status, String displayContent, String translatedContent) {
    Author displayAuthor = (status == CommentDisplayStatus.NORMAL) ? embed.getAuthor() : null;
    List<MentionedMember> displayMentions = (status == CommentDisplayStatus.NORMAL) ? embed.getMentionedMembers() : Collections.emptyList();

    return ReplyDto.builder()
        .replyId(embed.getReplyId())
        .author(displayAuthor)
        .content(displayContent)
        .likeCount(embed.getLikeCount())
        .status(status)
        .mentionedMembers(displayMentions)
        .translatedContent(translatedContent)
        .createdAt(embed.getCreatedAt())
        .modifiedAt(embed.getModifiedAt())
        .build();
  }

  /**
   * Reply JPA 엔티티를 기반으로 간단한 ReplyDto를 생성 (command)
   *
   * @param reply 대댓글 엔티티
   * @return 생성된 ReplyDto
   */
  public static ReplyDto of(Reply reply) {
    Member member = reply.getMember();
    Profile profile = member.getProfile();
    Author author = Author.of(member.getId(), profile.getNickname(), profile.getProfileImageUrl(), profile.getCountryOfResidence());
    return ReplyDto.builder()
        .parentCommentId(reply.getComment().getId())
        .replyId(reply.getId())
        .author(author)
        .status(CommentDisplayStatus.NORMAL)
        .content(reply.getContent())
        .createdAt(reply.getCreatedAt())
        .build();
  }
}