package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 피드가 스크랩에 대해 알 필요가 없을 것 같아 단방향 연관관계로 설정함
    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "is_scrap_canceled")
    private boolean isScrapCanceled = false;

    private Scrap(Member member, Feed feed, LocalDateTime createdAt) {
        this.member = member;
        this.feed = feed;
        this.createdAt = createdAt;
    }

    public static Scrap of(Member member, Feed feed, LocalDateTime createdAt) {
        return new Scrap(member, feed, createdAt);
    }

    public void softDelete() {
        this.isScrapCanceled = true;
    }

    public void cancelScrap() {
        this.isScrapCanceled = true;
    }
}
