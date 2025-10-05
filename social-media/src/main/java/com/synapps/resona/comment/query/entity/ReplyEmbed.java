package com.synapps.resona.comment.query.entity;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyEmbed {

  private Long replyId;
  private Author author;
  private Language language;
  private String content;
  private long likeCount;
  private List<MentionedMember> mentionedMembers;
  private List<Translation> translations;

  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private boolean isDeleted;

  private ReplyEmbed(Long replyId, Author author, Language language, String content,
      List<MentionedMember> mentionedMembers) {
    this.replyId = replyId;
    this.author = author;
    this.language = language;
    this.content = content;
    this.likeCount = 0;
    this.mentionedMembers = (mentionedMembers != null) ? mentionedMembers : new ArrayList<>();
    this.createdAt = LocalDateTime.now();
    this.modifiedAt = LocalDateTime.now();
    this.translations = new ArrayList<>();
    this.isDeleted = false;
  }

  public static ReplyEmbed of(Long replyId, Author author, Language language, String content,
      List<MentionedMember> mentionedMembers) {
    return new ReplyEmbed(replyId, author, language, content, mentionedMembers);
  }
}