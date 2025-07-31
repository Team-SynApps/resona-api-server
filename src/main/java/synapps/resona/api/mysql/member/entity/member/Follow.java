package synapps.resona.api.mysql.member.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "follow",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_follower_following_blocked",
            columnNames = {"follower_id", "following_id"}
        )
    }
)
@SQLRestriction("is_deleted = false")
public class Follow extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "follow_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id")
  private Member follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "following_id")
  private Member following;

  private Follow(Member follower, Member following) {
    this.follower = follower;
    this.following = following;
  }

  public static Follow of(Member follower, Member following) {
    return new Follow(follower, following);
  }
}