package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import synapps.resona.api.mysql.socialMedia.dto.media.FeedMediaDto;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.media.FeedMedia;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedDto {
  private final Long feedId;
  private final FeedMemberDto member;

  private final String content;
  private final int likeCount;
  private final List<FeedMediaDto> images;
  private final int totalCommentCount;
  private final LocalDateTime createdAt;

  public static FeedDto from(FeedWithCountsDto dto) {
    Feed feed = dto.getFeed();
    return FeedDto.builder()
        .feedId(feed.getId())
        .member(FeedMemberDto.from(feed.getMember()))
        .content(feed.getContent())
        .likeCount((int) dto.getLikeCount())
        .images(feed.getImages().stream().map(FeedMediaDto::from).toList()) // @BatchSize
        .totalCommentCount((int) dto.getTotalCommentCount())
        .createdAt(feed.getCreatedAt())
        .build();
  }
}
