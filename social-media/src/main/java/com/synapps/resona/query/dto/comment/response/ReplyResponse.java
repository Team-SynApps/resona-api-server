package com.synapps.resona.query.dto.comment.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.domain.entity.comment.Reply;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ReplyResponse {

  private Long commentId;

  private Long replyId;

  private String content;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Reply 엔티티를 ReplyPostResponse DTO로 변환하는 정적 팩토리 메서드
   * @param reply Reply 엔티티 객체
   * @param commentId 원본 댓글의 ID
   * @return 생성된 ReplyPostResponse DTO
   */
  public static ReplyResponse from(Reply reply, Long commentId) {
    return ReplyResponse.of(
        commentId,
        reply.getId(),
        reply.getContent(),
        reply.getCreatedAt()
    );
  }

  public static ReplyResponse from(Reply reply) {
    return ReplyResponse.of(
        reply.getComment().getId(),
        reply.getId(),
        reply.getContent(),
        reply.getCreatedAt());
  }
}
