package synapps.resona.api.mysql.member.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountStatus status;

    @NotNull
    private LocalDateTime lastAccessedAt;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    private AccountInfo(RoleType roleType, ProviderType providerType, AccountStatus status, LocalDateTime lastAccessedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roleType = roleType;
        this.providerType = providerType;
        this.status = status;
        this.lastAccessedAt = lastAccessedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountInfo of(RoleType roleType, ProviderType providerType, AccountStatus status, LocalDateTime lastAccessedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AccountInfo(roleType, providerType, status, lastAccessedAt, createdAt, updatedAt);
    }

}
