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

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private FeedImage(Feed feed, String url, LocalDateTime createdAt) {
        this.feed = feed;
        this.url = url;
        this.createdAt = createdAt;
    }

    public static FeedImage of(Feed feed, String url, LocalDateTime createdAt) {
        return new FeedImage(feed, url, createdAt);
    }

    public void softDelete() {
        isDeleted = true;
    }
}
