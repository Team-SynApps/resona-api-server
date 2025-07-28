package synapps.resona.api.mysql.member.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.mysql.member.entity.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class MemberNotificationSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_setting_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name="friend_request_enabled")
  private boolean friendRequestEnabled;

  @Column(name = "marketing_enabled")
  private boolean marketingEnabled;

  @Column(name = "service_notification_enabled")
  private boolean serviceNotificationEnabled;

  @Column(name = "sns_notification_enabled")
  private boolean socialNotificationEnabled;

  private MemberNotificationSetting(Member member, boolean friendRequestEnabled, boolean marketingEnabled,
      boolean serviceNotificationEnabled, boolean socialNotificationEnabled) {
    this.member = member;
    this.friendRequestEnabled = friendRequestEnabled;
    this.marketingEnabled = marketingEnabled;
    this.serviceNotificationEnabled = serviceNotificationEnabled;
    this.socialNotificationEnabled = socialNotificationEnabled;
  }

  public static MemberNotificationSetting of(Member member, boolean friendRequestEnabled,
      boolean marketingEnabled, boolean serviceNotificationEnabled,
      boolean socialNotificationEnabled) {
    return new MemberNotificationSetting(member, friendRequestEnabled, marketingEnabled,
        serviceNotificationEnabled, socialNotificationEnabled);
  }

  public void update(boolean friendRequestEnabled, boolean marketingEnabled,
      boolean serviceNotificationEnabled, boolean socialNotificationEnabled) {
    this.friendRequestEnabled = friendRequestEnabled;
    this.marketingEnabled = marketingEnabled;
    this.serviceNotificationEnabled = serviceNotificationEnabled;
    this.socialNotificationEnabled = socialNotificationEnabled;
  }
}
