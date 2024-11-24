package synapps.resona.api.mysql.member.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_info_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name="role_type")
    @NotNull
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name="provider_type")
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name="account_status")
    private AccountStatus status;

    @NotNull
    @Column(name="last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @NotNull
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    private AccountInfo(Member member, RoleType roleType, ProviderType providerType, AccountStatus status, LocalDateTime lastAccessedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.member = member;
        this.roleType = roleType;
        this.providerType = providerType;
        this.status = status;
        this.lastAccessedAt = lastAccessedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountInfo of(Member member, RoleType roleType, ProviderType providerType, AccountStatus status, LocalDateTime lastAccessedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AccountInfo(member, roleType, providerType, status, lastAccessedAt, createdAt, updatedAt);
    }

}
