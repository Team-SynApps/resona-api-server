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
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private final List<Like> likes = new ArrayList<>();

    // TODO: 이렇게 해도 되는지 검증이 필요함.
    @OneToMany(mappedBy = "feed", fetch = FetchType.EAGER)
    private final List<FeedMedia> images = new ArrayList<>();

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private FeedCategory category;

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
    private final boolean isKept = false;

    private Feed(Member member, String content, String category, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.member = member;
        this.content = content;
        this.category = FeedCategory.of(category);
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Feed of(Member member, String content, String category, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Feed(member, content, category, createdAt, modifiedAt);
    }

    public void softDelete() {
        this.isDeleted = true;
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}
