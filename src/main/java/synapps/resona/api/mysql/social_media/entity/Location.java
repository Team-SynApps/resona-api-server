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
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "feed_id") // 외래 키 컬럼 이름
    private Feed feed;

    @Column(name = "coordinate")
    private String coordinate;

    @Column(name = "address")
    private String address;

    @Column(name = "location_name")
    private String locationName;

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

    private Location(Feed feed, String coordinate, String address, String locationName, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.feed = feed;
        this.coordinate = coordinate;
        this.address = address;
        this.locationName = locationName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Location of(Feed feed, String coordinate, String address, String locationName, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Location(feed, coordinate, address, locationName, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
        this.modifiedAt = LocalDateTime.now();
    }
}
