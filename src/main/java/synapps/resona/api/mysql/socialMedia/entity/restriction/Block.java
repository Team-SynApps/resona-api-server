package synapps.resona.api.mysql.socialMedia.entity.restriction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Block extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "block_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blocker_id")
  private Member blocker;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blocked_id")
  private Member blocked;

  private Block(Member blocker, Member blocked) {
    this.blocker = blocker;
    this.blocked = blocked;
  }

  /**
   * Block 엔티티를 생성하는 정적 팩토리 메서드.
   * @param blocker 차단하는 사용자
   * @param blocked 차단당하는 사용자
   * @return 생성된 Block 객체
   */
  public static Block of(Member blocker, Member blocked) {
    return new Block(blocker, blocked);
  }
}

