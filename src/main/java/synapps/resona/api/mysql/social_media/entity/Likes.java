package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    // 멤버가 좋아요를 어디에 했는지 알 필요가 없어서 단방향 연관관계를 하려고 함
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    private Likes(Member member, Feed feed) {
        this.member = member;
        this.feed = feed;
    }

    public static Likes of(Member member, Feed feed) {
        return new Likes(member, feed);
    }

}
