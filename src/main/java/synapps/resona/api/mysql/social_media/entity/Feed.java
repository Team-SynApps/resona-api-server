package synapps.resona.api.mysql.social_media.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.dto.feed.FeedImageDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {
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

    @Column(name = "is_kept")
    private final boolean isKept = false;

    private Feed(Member member, String content, String category) {
        this.member = member;
        this.content = content;
        this.category = FeedCategory.of(category);
    }

    public static Feed of(Member member, String content, String category) {
        return new Feed(member, content, category);
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
