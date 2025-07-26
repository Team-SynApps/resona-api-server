package synapps.resona.api.mysql.socialMedia.dto.like.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.feed.Likes;

@Schema(description = "좋아요 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class LikeResponse {

  @Schema(description = "좋아요 고유 ID", example = "1")
  private Long likeId;

  @Schema(description = "좋아요를 누른 회원 ID", example = "101")
  private Long memberId;

  @Schema(description = "좋아요가 눌린 피드 ID", example = "202")
  private Long feedId;

  @Schema(description = "좋아요 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Likes 엔티티를 LikeResponse DTO로 변환하는 정적 팩토리 메서드
   * @param likes Likes 엔티티 객체
   * @return 생성된 LikeResponse DTO
   */
  public static LikeResponse from(Likes likes) {
    return LikeResponse.of(
        likes.getId(),
        likes.getMember().getId(),
        likes.getFeed().getId(),
        likes.getCreatedAt()
    );
  }
}