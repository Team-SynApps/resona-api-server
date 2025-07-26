package synapps.resona.api.mysql.socialMedia.dto.mention;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;

@Schema(description = "맨션 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class MentionResponseDto {

  @Schema(description = "맨션 고유 ID", example = "1")
  private Long mentionId;

  @Schema(description = "맨션된 회원 ID", example = "101")
  private Long memberId;

  @Schema(description = "맨션이 포함된 댓글 ID", example = "202")
  private Long commentId;

  @Schema(description = "맨션 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Mention 엔티티를 MentionResponseDto로 변환하는 정적 팩토리 메서드
   * @param mention Mention 엔티티 객체
   * @return 생성된 MentionResponseDto
   */
  public static MentionResponseDto from(Mention mention) {
    return MentionResponseDto.of(
        mention.getId(),
        mention.getMember().getId(),
        mention.getComment().getId(),
        mention.getCreatedAt()
    );
  }
}