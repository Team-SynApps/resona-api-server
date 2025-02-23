package synapps.resona.api.mysql.member.entity.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
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

    @Enumerated(EnumType.STRING)
    @Column(name="nationality")
    @NotNull
    private CountryCode nationality;

    @Enumerated(EnumType.STRING)
    @Column(name="country_of_residence")
    @NotNull
    private CountryCode countryOfResidence;

    @ElementCollection
    @CollectionTable(name = "language", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<Language> nativeLanguages = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "language", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<Language> interestingLanguages = new HashSet<>();

    @Size(max = 512)
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Size(max = 512)
    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Size(max = 512)
    @Column(name = "comment")
    private String comment;

    @Column(name="age")
    @NotNull
    private Integer age;

    @Column(name="birth")
    @NotNull
    private LocalDateTime birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    @NotNull
    private Gender gender;

    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private Profile(Member member,
                    String nickname,
                    CountryCode nationality,
                    CountryCode countryOfResidence,
                    Set<Language> nativeLanguages,
                    Set<Language> interestingLanguages,
                    String profileImageUrl,
                    String backgroundImageUrl,
                    String birth,
                    Gender gender,
                    String comment) {
        this.member = member;
        this.tag = generateTag(String.valueOf(member.getId()));
        this.nickname = nickname;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.nativeLanguages = nativeLanguages;
        this.interestingLanguages = interestingLanguages;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.birth = parseToLocalDate(birth);
        this.age = birthToAge(parseToLocalDate(birth));
        this.gender = gender;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void modifyProfile(String nickname,
                              CountryCode nationality,
                              CountryCode countryOfResidence,
                              Set<Language> nativeLanguages,
                              Set<Language> interestingLanguages,
                              String profileImageUrl,
                              String backgroundImageUrl,
                              String birth,
                              Gender gender,
                              String comment) {
        this.nickname = nickname;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.nativeLanguages = nativeLanguages;
        this.interestingLanguages = interestingLanguages;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.birth = parseToLocalDate(birth);
        this.age = birthToAge(parseToLocalDate(birth));
        this.gender = gender;
        this.comment = comment;
        this.modifiedAt = LocalDateTime.now();
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

    public static Profile of(Member member,
                             String nickname,
                             CountryCode nationality,
                             CountryCode countryOfResidence,
                             Set<Language> nativeLanguages,
                             Set<Language> interestingLanguages,
                             String profileImageUrl,
                             String backgroundImageUrl,
                             String birth,
                             Gender gender,
                             String comment) {
        return new Profile(member, nickname, nationality, countryOfResidence,
                nativeLanguages, interestingLanguages, profileImageUrl,
                backgroundImageUrl, birth, gender, comment);
    }

    private Integer birthToAge(LocalDateTime birth) {
        if(birth == null) {
            return 0;
        }
        int birthYear = birth.getYear();
        return LocalDateTime.now().getYear() - birthYear;
    }

    private LocalDateTime parseToLocalDate(String date) {
        try {
            return DateTimeUtil.stringToLocalDateTime(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd. Provided: " + date, e);
        }
    }
}
