package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    // 멤버가 좋아요를 어디에 했는지 알 필요가 없어서 단방향 연관관계를 하려고 함
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;


    @NotNull
    @Column(name = "liked_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime likedAt;

    @NotNull
    @Column(name = "canceled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime canceledAt;

    @NotNull
    @Column(name = "is_like_canceled")
    @Temporal(TemporalType.TIMESTAMP)
    private boolean isLikeCanceled = false;

    private Like(Member member, Feed feed, LocalDateTime likedAt) {
        this.member = member;
        this.feed = feed;
        this.likedAt = likedAt;
    }

    public static Like of(Member member, Feed feed, LocalDateTime likedAt) {
        return new Like(member, feed, likedAt);
    }

    public void cancelLike() {
        this.isLikeCanceled = true;
        this.canceledAt = LocalDateTime.now();
    }
}
