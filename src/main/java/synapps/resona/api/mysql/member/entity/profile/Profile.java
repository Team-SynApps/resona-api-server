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
import synapps.resona.api.mysql.member.util.HashGenerator;
import synapps.resona.api.mysql.member.util.MD5Generator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @NotNull
    @Size(min = 1, max = 50)
    private String tag;

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
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private Profile(Member member, String nickname, List<String> usingLanguages, String profileImageUrl, String backgroundImageUrl, String mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.member = member;
        this.tag = generateTag(String.valueOf(member.getId()));
        this.nickname = nickname;
        this.usingLanguages = parseUsingLanguages(usingLanguages);
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.mbti = MBTI.of(mbti);
        this.comment = comment;
        this.aboutMe = aboutMe;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void modifyProfile(String nickname, List<String> usingLanguages, String profileImageUrl, String backgroundImageUrl, String mbti, String comment, String aboutMe) {
        this.nickname = nickname;
        this.usingLanguages = parseUsingLanguages(usingLanguages);
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.mbti = MBTI.of(mbti);
        this.comment = comment;
        this.aboutMe = aboutMe;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeBackgroundUrl(String url) {
        this.backgroundImageUrl = url;
    }

    public void changeProfileImageUrl(String url) {
        this.profileImageUrl = url;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    private String generateTag(String input) {
        HashGenerator md5generator = new MD5Generator();
        return md5generator.generateHash(input);
    }


    private Set<Language> parseUsingLanguages(List<String> unParsedLanguages) {
        Set<Language> languages = new HashSet<>();
        for (String language : unParsedLanguages) {
            languages.add(Language.fromCode(language));
        }
        return languages;
    }

    public static Profile of(Member member, String nickname, List<String> usingLanguages, String profileImageUrl, String backgroundImageUrl, String mbti, String comment, String aboutMe, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Profile(member, nickname, usingLanguages, profileImageUrl, backgroundImageUrl, mbti, comment, aboutMe, createdAt, updatedAt);
    }
}
