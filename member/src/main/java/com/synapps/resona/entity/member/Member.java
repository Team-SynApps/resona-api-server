package com.synapps.resona.entity.member;

import com.synapps.resona.entity.account.AccountInfo;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.Profile;
import com.synapps.resona.entity.BaseEntity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @OneToMany(mappedBy = "follower")
  private final List<Follow> followings = new ArrayList<>();

  @OneToMany(mappedBy = "following")
  private final List<Follow> followers = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<MemberProvider> providers = new ArrayList<>();

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @Size(max = 120)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  private AccountInfo accountInfo;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "member_details_id")
  private MemberDetails memberDetails;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "profile_id")
  private Profile profile;

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

  private Member(String email, String password, LocalDateTime lastAccessedAt) {
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

  public static Member of(String email, String password, LocalDateTime lastAccessedAt) {
    return new Member(email, password, lastAccessedAt);
  }

  public void encodePassword(String rawPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.password = passwordEncoder.encode(rawPassword);
  }

  public void addProvider(MemberProvider provider) {
    providers.add(provider);
  }
}