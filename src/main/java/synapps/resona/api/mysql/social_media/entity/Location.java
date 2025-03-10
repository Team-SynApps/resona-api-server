package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseEntity {
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


    private Location(Feed feed, String coordinate, String address, String locationName) {
        this.feed = feed;
        this.coordinate = coordinate;
        this.address = address;
        this.locationName = locationName;
    }

    public static Location of(Feed feed, String coordinate, String address, String locationName) {
        return new Location(feed, coordinate, address, locationName);
    }

}
