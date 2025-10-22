package com.synapps.resona.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.query.entity.MemberStateDocument;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDto {

  private Long commentId;
  private Long feedId;
  private Author author;
  private String content;
  private long likeCount;
  private long replyCount;
  private CommentDisplayStatus status;
  private boolean hasLiked;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MentionedMember> mentionedMembers;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<ReplyDto> replies;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String translatedContent;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  /**
   * CommentDocument와 계산된 상태값, 처리된 대댓글 DTO 목록을 기반으로 CommentDto 생성 (query)
   * @param document MongoDB의 댓글 문서
   * @param status 동적으로 계산된 최종 표시 상태
   * @param displayContent 상태에 따라 가공된 최종 표시 내용
   * @param processedReplies 필터링 및 가공이 완료된 대댓글 DTO 목록
   * @return 생성된 CommentDto 객체
   */
  public static CommentDto from(
      CommentDocument document,
      CommentDisplayStatus status,
      boolean hasLiked,
      String displayContent,
      String translatedContent,
      List<ReplyDto> processedReplies) {
    Author displayAuthor = (status == CommentDisplayStatus.NORMAL) ? document.getAuthor() : null;
    List<MentionedMember> displayMentions = (status == CommentDisplayStatus.NORMAL) ? document.getMentionedMembers() : Collections.emptyList();

    return CommentDto.builder()
        .commentId(document.getCommentId())
        .feedId(document.getFeedId())
        .author(displayAuthor)
        .content(displayContent)
        .likeCount(document.getLikeCount())
        .replyCount(document.getReplyCount())
        .status(status)
        .hasLiked(hasLiked)
        .mentionedMembers(displayMentions)
        .replies(processedReplies)
        .translatedContent(translatedContent)
        .createdAt(document.getCreatedAt())
        .modifiedAt(document.getModifiedAt())
        .build();
  }

  /**
   * Comment JPA 엔티티와 추가 정보를 기반으로 CommentDto 생성 (query)
   *
   * @param comment 댓글 엔티티
   * @param status 댓글 표시 상태
   * @param content 표시될 내용
   * @param likeCount 좋아요 수
   * @param processedReplies 처리된 대댓글 목록
   * @return 생성된 CommentDto
   */
  public static CommentDto of(Comment comment, CommentDisplayStatus status, String content, long likeCount, List<ReplyDto> processedReplies) {
    Member member = comment.getMember();
    Profile profile = member.getProfile();
    Author author = Author.of(member.getId(), profile.getNickname(), profile.getProfileImageUrl(), profile.getCountryOfResidence());
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(author)
        .likeCount(likeCount)
        .content(content)
        .status(status)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .replies(processedReplies)
        .build();
  }

  /**
   * Comment JPA 엔티티를 기반으로 CommentDto 생성 (command)
   * 대댓글 목록은 포함하지 않습니다.
   *
   * @param comment 댓글 엔티티
   * @return 생성된 CommentDto
   */
  public static CommentDto of(Comment comment) {
    Member member = comment.getMember();
    Profile profile = member.getProfile();
    Author author = Author.of(member.getId(), profile.getNickname(), profile.getProfileImageUrl(), profile.getCountryOfResidence());
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(author)
        .content(comment.getContent())
        .status(CommentDisplayStatus.NORMAL)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .build();
  }
}