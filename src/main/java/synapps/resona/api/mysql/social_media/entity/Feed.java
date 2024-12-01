package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "feed")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<FeedImage> images = new ArrayList<>();

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

    @Column(name = "is_kept")
    private boolean isKept = false;

    private Feed(Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Feed of(Member member, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Feed(member, content, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void updateContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}
