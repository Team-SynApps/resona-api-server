package com.synapps.resona.command.entity.account;

import com.synapps.resona.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class AccountInfo extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_info_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_type")
  @NotNull
  private RoleType roleType;

  @Enumerated(EnumType.STRING)
  @NotNull
  @Column(name = "account_status")
  private AccountStatus status;

  @Column(name = "banned_until")
  private LocalDateTime bannedUntil;

  private AccountInfo(RoleType roleType, AccountStatus status) {
    this.roleType = roleType;
    this.status = status;
  }

  public static AccountInfo of(RoleType roleType, AccountStatus status) {
    return new AccountInfo(roleType, status);
  }

  public static AccountInfo empty() {
    return new AccountInfo(RoleType.GUEST, AccountStatus.TEMPORARY);
  }


  public void updateStatus(AccountStatus accountStatus) {
    this.status = accountStatus;
  }

  public void join() {
    this.status = AccountStatus.ACTIVE;
    this.roleType = RoleType.USER;
  }

  public void updateRoleType(RoleType roleType) {
    this.roleType = roleType;
  }

  public boolean isAccountTemporary() {
    return this.status.equals(AccountStatus.TEMPORARY);
  }

  public void ban(LocalDateTime until) {
    if (this.roleType == RoleType.ADMIN) {
      throw new IllegalStateException("관리자 계정은 정지할 수 없습니다.");
    }
    this.status = AccountStatus.BANNED;
    this.bannedUntil = until;
  }

  public void unban() {
    if (this.status == AccountStatus.BANNED) {
      this.status = AccountStatus.ACTIVE;
      this.bannedUntil = null;
    }
  }

  public boolean isBanExpired() {
    if (this.status != AccountStatus.BANNED || this.bannedUntil == null) {
      return false;
    }
    return LocalDateTime.now().isAfter(this.bannedUntil);
  }
}