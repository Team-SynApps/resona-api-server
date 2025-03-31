package synapps.resona.api.mysql.member.entity.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.util.HashGenerator;
import synapps.resona.api.mysql.member.util.MD5Generator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

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
    @Column(name = "nationality")
    @NotNull
    private CountryCode nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_of_residence")
    @NotNull
    private CountryCode countryOfResidence;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "native_languages", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "native_languages")
    private Set<Language> nativeLanguages = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "interesting_languages", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "interesting_languages")
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

    @Column(name = "age")
    @NotNull
    private Integer age;

    @Column(name = "birth")
    @NotNull
    private LocalDateTime birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    @NotNull
    private Gender gender;

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
    }

    private Profile(Member member,
                    CountryCode nationality,
                    CountryCode countryOfResidence,
                    Set<Language> nativeLanguages,
                    Set<Language> interestingLanguages,
                    String nickname,
                    String profileImageUrl,
                    String birth) {
        this.member = member;
        this.tag = generateTag(String.valueOf(member.getId()));
        this.nickname = nickname;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.nativeLanguages = nativeLanguages;
        this.interestingLanguages = interestingLanguages;
        this.profileImageUrl = profileImageUrl;
        this.birth = parseToLocalDate(birth);
        this.age = birthToAge(parseToLocalDate(birth));
        this.backgroundImageUrl = "";
        this.gender = Gender.NOT_DECIDED;
        this.comment = "";
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

    // 회원가입용 profile
    public static Profile of(Member member,
                             CountryCode nationality,
                             CountryCode countryOfResidence,
                             Set<Language> nativeLanguages,
                             Set<Language> interestingLanguages,
                             String nickname,
                             String profileImageUrl,
                             String birth) {
        return new Profile(member, nationality, countryOfResidence, nativeLanguages, interestingLanguages, nickname, profileImageUrl, birth);
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
    }

    public void changeBackgroundUrl(String url) {
        this.backgroundImageUrl = url;
    }

    public void changeProfileImageUrl(String url) {
        this.profileImageUrl = url;
    }

    public void changeGender(String gender) {
        this.gender = Gender.of(gender);
    }

    private String generateTag(String input) {
        HashGenerator md5generator = new MD5Generator();
        return md5generator.generateHash(input);
    }

    private Integer birthToAge(LocalDateTime birth) {
        if (birth == null) {
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
