package synapps.resona.api.mysql.member.entity.profile;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(max = 512)
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Size(max = 512)
    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "MBTI")
    @Nullable
    private MBTI mbti;

    @Size(max = 512)
    @Column(name = "comment")
    private String comment;

    @Column(name = "about_me")
    private String aboutMe;

    @NotNull
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    private Profile(String profileImageUrl, String backgroundImageUrl, MBTI mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.mbti = mbti;
        this.comment = comment;
        this.aboutMe = aboutMe;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Profile of(String profileImageUrl, String backgroundImageUrl, MBTI mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Profile(profileImageUrl, backgroundImageUrl, mbti, comment, aboutMe, createdAt, updatedAt);
    }
}
