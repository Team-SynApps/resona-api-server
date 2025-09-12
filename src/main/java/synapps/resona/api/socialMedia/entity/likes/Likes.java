package synapps.resona.api.socialMedia.entity.likes;

import jakarta.persistence.*;
import lombok.Getter;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.member.entity.member.Member;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "like_type")
public abstract class Likes extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "like_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id")
  private Member member;

  protected void setMember(Member member) {
    this.member = member;
  }
}