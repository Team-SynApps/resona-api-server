package com.synapps.resona.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.comment.command.entity.Mention;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class MentionResponse {

  private Long mentionId;

  private Long memberId;

  private Long commentId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Mention 엔티티를 MentionResponseDto로 변환하는 정적 팩토리 메서드
   * @param mention Mention 엔티티 객체
   * @return 생성된 MentionResponseDto
   */
  public static MentionResponse from(Mention mention) {
    return MentionResponse.of(
        mention.getId(),
        mention.getMentionedMember().getId(),
        mention.getMentionedMember().getId(),
        mention.getCreatedAt()
    );
  }
}