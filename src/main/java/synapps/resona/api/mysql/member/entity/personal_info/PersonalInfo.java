package synapps.resona.api.mysql.member.entity.personal_info;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.CountryCode;
import synapps.resona.api.mysql.member.entity.Language;
import synapps.resona.api.mysql.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @ElementCollection
    @CollectionTable(name = "language", joinColumns = @JoinColumn(name = "personal_info_id"))
    private List<Language> usingLanguages;

    @Enumerated(EnumType.STRING)
    @Column(name="nationality")
    @NotNull
    private CountryCode nationality;

    @Enumerated(EnumType.STRING)
    @Column(name="country_of_residence")
    @NotNull
    private CountryCode countryOfResidence;

    @Size(max = 20)
    @Column(name="phone_number")
    @NotNull
    private String phoneNumber;

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

    @Nullable
    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    private PersonalInfo(CountryCode nationality,List<Language> usingLanguages, CountryCode countryOfResidence, String phoneNumber, LocalDateTime birth, Gender gender, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.nationality = nationality;
        this.usingLanguages = usingLanguages;
        this.countryOfResidence = countryOfResidence;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.age = birthToAge(birth);
        this.gender = gender;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PersonalInfo of(CountryCode nationality,List<Language> usingLanguages, CountryCode countryOfResidence, String phoneNumber, LocalDateTime birth, Gender gender, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new PersonalInfo(nationality, usingLanguages, countryOfResidence, phoneNumber, birth, gender, location, createdAt, updatedAt);
    }

    private Integer birthToAge(LocalDateTime birth) {
        if(birth == null) {
            return 0;
        }
        int birthYear = birth.getYear();
        return LocalDateTime.now().getYear() - birthYear;
    }

}
