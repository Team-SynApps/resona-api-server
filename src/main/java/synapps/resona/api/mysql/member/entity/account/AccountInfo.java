package synapps.resona.api.mysql.member.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import synapps.resona.api.oauth.entity.ProviderType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_info_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

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

    private AccountInfo(Member member, RoleType roleType, ProviderType providerType, AccountStatus status) {
        this.member = member;
        this.roleType = roleType;
        this.providerType = providerType;
        this.status = status;
    }

    public static AccountInfo of(Member member, RoleType roleType, ProviderType providerType, AccountStatus status) {
        return new AccountInfo(member, roleType, providerType, status);
    }

    public void updateStatus(AccountStatus accountStatus) {
        this.status = accountStatus;
    }

    public void updateRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public boolean isAccountTemporary() {
        return this.status.equals(AccountStatus.TEMPORARY);
    }

}
