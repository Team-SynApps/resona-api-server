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
    private Long id;

    @NotBlank
    @Size(max = 15)
    private String nickname;

    private Integer timezone;

    @NotNull
    private Boolean isOnline;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 120)
    private String password;
//
//    @Enumerated(EnumType.STRING)
//    private List<Languages> interestedLanguages;

    @Enumerated(EnumType.STRING)
    @Nullable
    private Category category;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime modifiedAt;

    @NotNull
    private LocalDateTime lastAccessedAt;

    private Member(String nickname,
                   Integer timezone,
                   String comment,
                   Boolean isOnline,
                   String email,
                   String password,
                   LocalDateTime createdAt,
                   LocalDateTime modifiedAt,
                   LocalDateTime lastAccessedAt) {
        this.nickname = nickname;
        this.timezone = timezone;
        this.comment = comment;
        this.isOnline = isOnline;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.lastAccessedAt = lastAccessedAt;
    }

    public static Member of(String nickname,
                            Integer timezone,
                            String comment,
                            Boolean isOnline,
                            String email,
                            String password,
                            LocalDateTime createdAt,
                            LocalDateTime modifiedAt,
                            LocalDateTime lastAccessedAt) {
        return new Member(nickname, timezone, comment, isOnline, email, password, createdAt, modifiedAt, lastAccessedAt);
    }

    // 선택적 필드를 위한 추가 of 메소드
    public static Member ofWithOptionals(String nickname,
                                         Integer timezone,
                                         String comment,
                                         Boolean isOnline,
                                         String email,
                                         String password,
                                         LocalDateTime createdAt,
                                         LocalDateTime modifiedAt,
                                         LocalDateTime lastAccessedAt,
                                         Category category,
                                         String profileImageUrl) {
        Member member = new Member(nickname, timezone, comment, isOnline, email, password, createdAt, modifiedAt, lastAccessedAt);
        member.category = category;
        member.profileImageUrl = profileImageUrl;
        return member;
    }
    public void setUserNickname(String name) {
        this.nickname = name;
    }

    public void encodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}