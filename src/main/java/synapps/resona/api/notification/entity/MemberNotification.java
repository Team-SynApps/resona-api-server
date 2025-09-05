package synapps.resona.api.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.member.entity.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class MemberNotification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @NotNull
  @Column(name = "title")
  private String title;

  @NotNull
  @Column(name = "body")
  private String body;

  @Column(name = "image")
  private String image;

  @Column(name = "icon")
  private String icon;

  @Column(name = "is_read")
  private boolean isRead;


  @Builder
  private MemberNotification(Member member, String title, String body, String image, String icon) {
    this.member = member;
    this.title = title;
    this.body = body;
    this.image = image;
    this.icon = icon;
    this.isRead = false; // 기본값은 false
  }

  public void read() {
    this.isRead = true;
  }
}
