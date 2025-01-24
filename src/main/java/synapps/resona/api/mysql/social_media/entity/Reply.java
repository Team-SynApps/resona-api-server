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
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name="content")
    private String content;

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

    private Reply(Comment comment, Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.comment = comment;
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Reply of(Comment comment, Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Reply(comment, member, content, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
        this.modifiedAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}
