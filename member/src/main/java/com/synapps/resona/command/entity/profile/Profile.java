package com.synapps.resona.command.entity.profile;

import com.synapps.resona.entity.BaseEntity;
import com.synapps.resona.entity.Language;
import com.synapps.resona.utils.DateTimeUtil;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Profile extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "profile_id")
  private Long id;

  @NotNull
  @Size(min = 1, max = 30)
  @Pattern(regexp = "^[a-zA-Z_]+$", message = "태그는 영문과 언더스코어(_)만 사용할 수 있습니다.")
  private String tag;

  @NotNull
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


  // 생성자 - 일반 프로필 생성
  private Profile(String nickname,
      String tag,
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
    this.tag = tag;
    this.nationality = nationality;
    this.countryOfResidence = countryOfResidence;
    this.nativeLanguages = nativeLanguages;
    this.interestingLanguages = interestingLanguages;
    this.profileImageUrl = profileImageUrl;
    this.backgroundImageUrl = backgroundImageUrl;
    this.birth = parseToLocalDate(birth);
    this.age = birthToAge(this.birth);
    this.gender = gender;
    this.comment = comment;
  }

  // 회원가입용 프로필 생성
  private Profile(CountryCode nationality,
      CountryCode countryOfResidence,
      Set<Language> nativeLanguages,
      Set<Language> interestingLanguages,
      String nickname,
      String tag,
      String profileImageUrl,
      String birth) {

    this.nationality = nationality;
    this.countryOfResidence = countryOfResidence;
    this.nativeLanguages = nativeLanguages;
    this.interestingLanguages = interestingLanguages;
    this.nickname = nickname;
    this.tag = tag;
    this.profileImageUrl = profileImageUrl;
    this.birth = parseToLocalDate(birth);
    this.age = birthToAge(this.birth);
    this.backgroundImageUrl = "";
    this.gender = Gender.NOT_DECIDED;
    this.comment = "";
  }

  // 일반 프로필 생성
  public static Profile of(String nickname,
      String tag,
      CountryCode nationality,
      CountryCode countryOfResidence,
      Set<Language> nativeLanguages,
      Set<Language> interestingLanguages,
      String profileImageUrl,
      String backgroundImageUrl,
      String birth,
      Gender gender,
      String comment) {

    return new Profile(nickname, tag, nationality, countryOfResidence,
        nativeLanguages, interestingLanguages, profileImageUrl,
        backgroundImageUrl, birth, gender, comment);
  }

  // 회원가입용
  public static Profile of(CountryCode nationality,
      CountryCode countryOfResidence,
      Set<Language> nativeLanguages,
      Set<Language> interestingLanguages,
      String nickname,
      String tag,
      String profileImageUrl,
      String birth) {

    return new Profile(nationality, countryOfResidence,
        nativeLanguages, interestingLanguages,
        nickname, tag, profileImageUrl, birth);
  }

  // 빈 프로필
  public static Profile empty() {
    return new Profile("", "none",
        CountryCode.NOT_DEFINED, CountryCode.NOT_DEFINED,
        new HashSet<>(), new HashSet<>(),
        "", "", "2000-01-01",
        Gender.NOT_DECIDED, "");
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

  public void join(String nickname,
      CountryCode nationality,
      CountryCode countryOfResidence,
      Set<Language> nativeLanguages,
      Set<Language> interestingLanguages,
      String profileImageUrl,
      String birth) {
    this.nickname = nickname;
    this.nationality = nationality;
    this.countryOfResidence = countryOfResidence;
    this.nativeLanguages = nativeLanguages;
    this.interestingLanguages = interestingLanguages;
    this.profileImageUrl = profileImageUrl;
    this.birth = parseToLocalDate(birth);
    this.age = birthToAge(parseToLocalDate(birth));
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

  public void registerTag(String tag) {
    this.tag = tag;
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
      throw new IllegalArgumentException(
          "Invalid date format. Expected format: yyyy-MM-dd. Provided: " + date, e);
    }
  }
}
