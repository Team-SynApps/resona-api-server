package synapps.resona.api.mysql.socialMedia.entity.feed;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // NOTE: 피드가 스크랩에 대해 알 필요가 없을 것 같아 단방향 연관관계로 설정함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    private Scrap(Member member, Feed feed, LocalDateTime createdAt) {
        this.member = member;
        this.feed = feed;
    }

    public static Scrap of(Member member, Feed feed, LocalDateTime createdAt) {
        return new Scrap(member, feed, createdAt);
    }

}
