package synapps.resona.api.mysql.member.entity.member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.notification.MemberNotificationSetting;
import synapps.resona.api.mysql.member.entity.notification.MemberPushToken;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;
import synapps.resona.api.mysql.socialMedia.entity.report.FeedReport;
import synapps.resona.api.mysql.socialMedia.entity.report.Report;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Block;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Hide;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @OneToMany(mappedBy = "member")
  private final List<Feed> feeds = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Mention> mentions = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Scrap> scraps = new ArrayList<>();

  @OneToMany(mappedBy = "follower")
  private final List<Follow> followings = new ArrayList<>();

  @OneToMany(mappedBy = "following")
  private final List<Follow> followers = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<MemberPushToken> pushTokens = new ArrayList<>();

  @OneToMany(mappedBy = "reporter")
  private final List<Report> reportsMade = new ArrayList<>();

  @OneToMany(mappedBy = "reported")
  private final List<Report> reportsReceived = new ArrayList<>();

  @OneToMany(mappedBy = "blocker")
  private final List<Block> blocksMade = new ArrayList<>();

  @OneToMany(mappedBy = "blocked")
  private final List<Block> blocksReceived = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<Hide> hiddenContents = new ArrayList<>();

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @Size(max = 120)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "account_info_id")
  private AccountInfo accountInfo;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "member_details_id")
  private MemberDetails memberDetails;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "profile_id")
  private Profile profile;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "notification_setting_id")
  private MemberNotificationSetting notificationSetting;

  @NotNull
  @Column(name = "last_accessed_at")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime lastAccessedAt;

  private Member(AccountInfo accountInfo,
      String email,
      String password,
      LocalDateTime lastAccessedAt) {
    this.accountInfo = accountInfo;
    this.email = email;
    this.password = password;
    this.lastAccessedAt = lastAccessedAt;
  }

  private Member(AccountInfo accountInfo,
      MemberDetails memberDetails,
      Profile profile,
      String email,
      String password,
      LocalDateTime lastAccessedAt) {
    this.accountInfo = accountInfo;
    this.memberDetails = memberDetails;
    this.profile = profile;
    this.email = email;
    this.password = password;
    this.lastAccessedAt = lastAccessedAt;
  }

  public static Member of(AccountInfo accountInfo,
      String email,
      String password,
      LocalDateTime lastAccessedAt) {
    return new Member(accountInfo, email, password, lastAccessedAt);
  }

  public static Member of(AccountInfo accountInfo,
      MemberDetails memberDetails,
      Profile profile,
      String email,
      String password,
      LocalDateTime lastAccessedAt) {
    return new Member(accountInfo, memberDetails, profile, email, password, lastAccessedAt);
  }

  public void encodePassword(String rawPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.password = passwordEncoder.encode(rawPassword);
  }

  public void addNotificationSetting(MemberNotificationSetting memberNotificationSetting) {
    this.notificationSetting = memberNotificationSetting;
  }
}