package synapps.resona.api.mysql.member.entity;

import lombok.Setter;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Mention;
import synapps.resona.api.mysql.social_media.entity.Scrap;
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
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "member")
    private List<Feed> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Mention> mentions = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Scrap> scraps = new ArrayList<>();

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "modified_at")
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