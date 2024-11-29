package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reply")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "mention")
    private List<Mention> mentions = new ArrayList<>();

    @Column(name="content")
    private String content;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "is_reply_exist")
    private boolean isReplyExist = false;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private Comment(Feed feed, Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.feed = feed;
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Comment of(Feed feed, Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Comment(feed, member, content, createdAt, modifiedAt);
    }

    public void softDelete() {
        isDeleted = true;
    }
}
