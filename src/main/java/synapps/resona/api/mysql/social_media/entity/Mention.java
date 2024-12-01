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
public class Mention {
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

    @NotNull
    @Column(name = "mentioned_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime mentionedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private Mention(Member member, Comment comment, LocalDateTime mentionedAt) {
        this.member = member;
        this.comment = comment;
        this.mentionedAt = mentionedAt;
    }

    public static Mention of(Member member, Comment comment, LocalDateTime mentionedAt) {
        return new Mention(member, comment, mentionedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
