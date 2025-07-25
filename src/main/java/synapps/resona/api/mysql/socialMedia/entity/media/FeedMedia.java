package synapps.resona.api.mysql.socialMedia.entity.media;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedMedia extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feed_media_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  @Column(name = "media_type")
  @Enumerated(EnumType.STRING)
  private MediaType mediaType;

  @Column(name = "feed_media_url")
  private String url;

  @NotNull
  @Column(name = "media_index")
  private Integer index;

  private FeedMedia(Feed feed, String mediaType, String url, Integer index) {
    this.feed = feed;
    this.mediaType = MediaType.of(mediaType);
    this.url = url;
    this.index = index;
  }

  public static FeedMedia of(Feed feed, String mediaType, String url, Integer index) {
    return new FeedMedia(feed, mediaType, url, index);
  }

  public void changeIndex(Integer index) {
    this.index = index;
  }

  // url 업데이트 된 걸 로그로 남겨야 할지
  public void updateUrl(String url) {
    this.url = url;
  }
}
