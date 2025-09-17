package synapps.resona.api.socialMedia.feed.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.socialMedia.media.dto.FeedMediaDto;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.feed.entity.FeedCategory;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedDto {
  private final Long feedId;
  private final SocialMemberDto author;
  private FeedCategory category;
  private String languageCode;
  private final String content;
  private final List<FeedMediaDto> images;

  private final long likeCount;
  private final long commentCount;

  private boolean hasLiked;
  private boolean hasScraped;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private final LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private final LocalDateTime modifiedAt;

  public static FeedDto from(FeedDetailDto dto) {
    Feed feed = dto.getFeed();
    return FeedDto.builder()
        .feedId(feed.getId())
        .author(SocialMemberDto.from(feed.getMember()))
        .content(feed.getContent())
        .category(feed.getCategory())
        .languageCode(feed.getLanguage().getCode())
        .likeCount((int) dto.getMetaData().getLikeCount())
        .images(feed.getImages().stream().map(FeedMediaDto::from).toList()) // @BatchSize
        .commentCount((int) dto.getMetaData().getCommentCount())
        .hasLiked(dto.getMetaData().isHasLiked())
        .hasScraped(dto.getMetaData().isHasScraped())
        .createdAt(feed.getCreatedAt())
        .modifiedAt(feed.getModifiedAt())
        .build();
  }
}
