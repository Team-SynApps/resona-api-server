package synapps.resona.api.mysql.member.entity.profile;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.Language;
import synapps.resona.api.mysql.member.entity.Member;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @NotBlank
    @Size(max = 15)
    private String nickname;

    @ElementCollection
    @CollectionTable(name = "language", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<Language> usingLanguages = new HashSet<>();

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

    private Profile(Member member,String nickname, Set<Language> usingLanguages, String profileImageUrl, String backgroundImageUrl, MBTI mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.member = member;
        this.nickname = nickname;
        this.usingLanguages = usingLanguages;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.mbti = mbti;
        this.comment = comment;
        this.aboutMe = aboutMe;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Profile of(Member member,String nickname, Set<Language> usingLanguages, String profileImageUrl, String backgroundImageUrl, MBTI mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Profile(member, nickname, usingLanguages, profileImageUrl, backgroundImageUrl, mbti, comment, aboutMe, createdAt, updatedAt);
    }
}
