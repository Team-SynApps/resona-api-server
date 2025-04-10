package synapps.resona.api.mysql.socialMedia.entity.feedComplaint;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComplaint extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_complaint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complainer_id")
    private Member complainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complained_id")
    private Member complainTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "complains")
    private Complains complains;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked = false;

    private FeedComplaint(Member complainer, Member complainTo, Feed feed, Complains complains, boolean isBlocked) {
        this.complainer = complainer;
        this.complainTo = complainTo;
        this.feed = feed;
        this.complains = complains;
        this.isBlocked = isBlocked;
    }

    public static FeedComplaint of(Member complainer, Member complainTo, Feed feed, Complains complains, boolean isBlocked) {
        return new FeedComplaint(complainer, complainTo, feed, complains, isBlocked);
    }
}
