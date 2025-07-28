package synapps.resona.api.mysql.member.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.oauth.entity.ProviderType;

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
  @Column(name = "provider_type")
  private ProviderType providerType;

  @Enumerated(EnumType.STRING)
  @NotNull
  @Column(name = "account_status")
  private AccountStatus status;

  private AccountInfo(RoleType roleType, ProviderType providerType, AccountStatus status) {
    this.roleType = roleType;
    this.providerType = providerType;
    this.status = status;
  }

  public static AccountInfo of(RoleType roleType, ProviderType providerType, AccountStatus status) {
    return new AccountInfo(roleType, providerType, status);
  }

  public static AccountInfo empty() {
    return new AccountInfo(RoleType.GUEST, ProviderType.LOCAL, AccountStatus.TEMPORARY);
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

}
