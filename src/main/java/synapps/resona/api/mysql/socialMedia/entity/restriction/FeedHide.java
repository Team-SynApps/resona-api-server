package synapps.resona.api.mysql.socialMedia.entity.restriction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("FEED")
public class FeedHide extends Hide {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  private FeedHide(Member member, Feed feed) {
    this.setMember(member);
    this.feed = feed;
  }

  /**
   * FeedHide 엔티티를 생성하는 정적 팩토리 메서드.
   * @param member 숨김 처리한 사용자
   * @param feed 숨김 대상 피드
   * @return 생성된 FeedHide 객체
   */
  public static FeedHide of(Member member, Feed feed) {
    return new FeedHide(member, feed);
  }
}