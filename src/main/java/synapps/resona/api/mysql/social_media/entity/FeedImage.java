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
public class FeedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(name = "feed_image_url")
    private String url;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @NotNull
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private FeedImage(Feed feed, String url, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.feed = feed;
        this.url = url;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FeedImage of(Feed feed, String url, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new FeedImage(feed, url, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    // url 업데이트 된 걸 로그로 남겨야 할지
    public void updateUrl(String url) {
        this.url = url;
        this.modifiedAt = LocalDateTime.now();
    }
}
