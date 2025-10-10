package com.synapps.resona.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.common.entity.Author;
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
  private List<MentionedMember> mentionedMembers;
  private List<ReplyDto> replies;
  private String translatedContent;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  /**
   * CommentDocument와 계산된 상태값, 처리된 대댓글 DTO 목록을 기반으로 최종 CommentDto를 생성한다.
   * @param document MongoDB의 댓글 문서
   * @param status 동적으로 계산된 최종 표시 상태
   * @param displayContent 상태에 따라 가공된 최종 표시 내용
   * @param processedReplies 필터링 및 가공이 완료된 대댓글 DTO 목록
   * @return 생성된 CommentDto 객체
   */
  public static CommentDto from(CommentDocument document, CommentDisplayStatus status, String displayContent, String translatedContent, List<ReplyDto> processedReplies) {
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
        .mentionedMembers(displayMentions)
        .replies(processedReplies)
        .translatedContent(translatedContent)
        .createdAt(document.getCreatedAt())
        .modifiedAt(document.getModifiedAt())
        .build();
  }
}