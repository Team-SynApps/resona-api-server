package synapps.resona.api.mysql.member.entity;

import lombok.Setter;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 120)
    private String password;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime modifiedAt;

    private Member(String email,
                   String password,
                   LocalDateTime createdAt,
                   LocalDateTime modifiedAt) {
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Member of(String email,
                            String password,
                            LocalDateTime createdAt,
                            LocalDateTime modifiedAt) {
        return new Member(email, password, createdAt, modifiedAt);
    }

    public void encodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}