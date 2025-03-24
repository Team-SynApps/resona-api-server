package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mention extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mention_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private Mention(Member member, Comment comment, LocalDateTime mentionedAt) {
        this.member = member;
        this.comment = comment;
    }

    public static Mention of(Member member, Comment comment, LocalDateTime mentionedAt) {
        return new Mention(member, comment, mentionedAt);
    }

}
