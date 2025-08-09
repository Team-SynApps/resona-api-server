package synapps.resona.api.member.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.member.entity.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class MemberPushToken extends BaseEntity {

  @Id
  @Column(name = "device_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "fcm_token")
  private String fcmToken;

  @Column(name = "device_info")
  private String deviceInfo;

  @Column(name = "is_active")
  private boolean isActive;

  private MemberPushToken(Long deviceId, Member member, String fcmToken, String deviceInfo,
      boolean isActive) {
    this.id = deviceId;
    this.member = member;
    this.fcmToken = fcmToken;
    this.deviceInfo = deviceInfo;
    this.isActive = isActive;
  }

  public static MemberPushToken of(Long deviceId, Member member, String fcmToken, String deviceInfo,
      boolean isActive) {
    return new MemberPushToken(deviceId, member, fcmToken, deviceInfo, isActive);
  }
}
