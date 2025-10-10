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
    private String placeId;
    private String displayName;
    private String formattedAddress;
    private GeoLocation location;
    private String primaryType;

    @Getter
    public static class GeoLocation {
        private double latitude;
        private double longitude;

        private GeoLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public static GeoLocation of(double latitude, double longitude) {
            return new GeoLocation(latitude, longitude);
        }
    }

    private LocationEmbed(String placeId, String displayName, String formattedAddress, GeoLocation location, String primaryType) {
        this.placeId = placeId;
        this.displayName = displayName;
        this.formattedAddress = formattedAddress;
        this.location = location;
        this.primaryType = primaryType;
    }

    public static LocationEmbed of(String placeId, String displayName, String formattedAddress, GeoLocation location, String primaryType) {
        return new LocationEmbed(placeId, displayName, formattedAddress, location, primaryType);
    }
  }
}
