package com.synapps.resona.retrieval.query.entity;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.BaseDocument;
import com.synapps.resona.entity.Language;
import com.synapps.resona.feed.command.entity.FeedCategory;

import com.synapps.resona.feed.command.entity.MediaType;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "feeds")
@CompoundIndexes({
    @CompoundIndex(name = "category_created_idx", def = "{'category': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "author_member_created_idx", def = "{'author.memberId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "author_country_created_idx", def = "{'author.countryOfResidence': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "author_country_category_created_idx", def = "{'author.countryOfResidence': 1, 'category': 1, 'createdAt': -1}")
})
public class FeedDocument extends BaseDocument {

  @Id
  private ObjectId _id;

  @Indexed(unique = true)
  private Long feedId;

  private Author author;
  private Language language;
  private String content;
  private List<MediaEmbed> medias;
  private LocationEmbed location;
  private FeedCategory category;
  private List<Translation> translations;

  @Field("like_count")
  private long likeCount;

  @Field("comment_count")
  private long commentCount;

  private FeedStatus status;

  private List<Long> hiddenByMemberIds;

  private FeedDocument(Long feedId, Author author, Language language, String content, List<MediaEmbed> medias,
      LocationEmbed location, FeedCategory category, List<Translation> translations) {
    this.feedId = feedId;
    this.author = author;
    this.language = language;
    this.content = content;
    this.medias = medias;
    this.location = location;
    this.category = category;
    this.translations = translations;
    this.likeCount = 0;
    this.commentCount = 0;
    this.status = FeedStatus.ACTIVE;
    this.hiddenByMemberIds = new ArrayList<>();
  }

  public static FeedDocument of(Long feedId, Author author, String content, List<MediaEmbed> medias,
      LocationEmbed location, FeedCategory category, Language language,
      List<Translation> translations) {
    return new FeedDocument(feedId, author, language, content, medias, location, category,
        translations);
  }

  // TODO: 도메인에 함수 제거, 쿼리를 따로 작성하는 것을 추천 -> 원자성 보장
  public void addTranslation(Translation translation) {
    translations.add(translation);
  }

  // TODO: 도메인에 함수 제거, 쿼리를 따로 작성하는 것을 추천 -> 원자성 보장
  public void updateContent(String content) {
    this.content = content;
  }

  @Getter
  public static class MediaEmbed {
    private MediaType mediaType;
    private String url;
    private int index;

    private MediaEmbed(MediaType mediaType, String url, int index) {
      this.mediaType = mediaType;
      this.url = url;
      this.index = index;
    }

    public static MediaEmbed of(MediaType mediaType, String url, int index) {
      return new MediaEmbed(mediaType, url, index);
    }
  }

  @Getter
  public static class LocationEmbed {
    private String coordinate;
    private String address;
    private String locationName;

    private LocationEmbed(String coordinate, String address, String locationName) {
      this.coordinate = coordinate;
      this.address = address;
      this.locationName = locationName;
    }

    public static LocationEmbed of(String coordinate, String address, String locationName) {
      return new LocationEmbed(coordinate, address, locationName);
    }
  }
}
