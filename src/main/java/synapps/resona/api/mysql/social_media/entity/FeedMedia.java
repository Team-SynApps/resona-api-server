package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_media_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(name="media_type")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "feed_media_url")
    private String url;

    @NotNull
    @Column(name="media_index")
    private Integer index;

    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;


    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private FeedMedia(Feed feed, String mediaType, String url, Integer index, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.feed = feed;
        this.mediaType = MediaType.of(mediaType);
        this.url = url;
        this.index = index;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FeedMedia of(Feed feed, String mediaType, String url, Integer index, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new FeedMedia(feed, mediaType, url, index, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
        this.modifiedAt = LocalDateTime.now();
    }

    public void changeIndex(Integer index) {
        this.index = index;
    }

    // url 업데이트 된 걸 로그로 남겨야 할지
    public void updateUrl(String url) {
        this.url = url;
        this.modifiedAt = LocalDateTime.now();
    }
}
