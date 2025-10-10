package com.synapps.resona.comment.query.entity;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.BaseDocument;
import com.synapps.resona.entity.Language;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "comments")
public class CommentDocument extends BaseDocument {

  @Id
  private ObjectId _id;

  @Indexed(unique = true)
  private Long commentId;

  @Indexed
  private Long feedId;

  private Author author;
  private Language language;
  private String content;

  @Field("like_count")
  private long likeCount;

  @Field("reply_count")
  private long replyCount;

  private List<MentionedMember> mentionedMembers;

  private List<ReplyEmbed> replies;

  private List<Translation> translations;

  private CommentDocument(Long commentId, Long feedId, Author author, Language language,
      String content, List<MentionedMember> mentionedMembers) {
    this.commentId = commentId;
    this.feedId = feedId;
    this.author = author;
    this.language = language;
    this.content = content;
    this.mentionedMembers = (mentionedMembers != null) ? mentionedMembers : new ArrayList<>();
    this.likeCount = 0;
    this.replyCount = 0;
    this.replies = new ArrayList<>();
    this.translations = new ArrayList<>();
  }

  private CommentDocument(Long commentId, Long feedId, Author author, Language language,
      String content, List<MentionedMember> mentionedMembers, List<Translation> translations) {
    this.commentId = commentId;
    this.feedId = feedId;
    this.author = author;
    this.language = language;
    this.content = content;
    this.mentionedMembers = (mentionedMembers != null) ? mentionedMembers : new ArrayList<>();
    this.likeCount = 0;
    this.replyCount = 0;
    this.replies = new ArrayList<>();
    this.translations = translations;
  }

  public static CommentDocument of(Long commentId, Long feedId, Author author, Language language,
      String content, List<MentionedMember> mentionedMembers) {
    return new CommentDocument(commentId, feedId, author, language, content, mentionedMembers);
  }

  public static CommentDocument of(Long commentId, Long feedId, Author author, Language language,
      String content, List<MentionedMember> mentionedMembers, List<Translation> translations) {
    return new CommentDocument(commentId, feedId, author, language, content, mentionedMembers, translations);
  }
}