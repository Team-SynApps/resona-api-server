package synapps.resona.api.mysql.member.entity.personal_info;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.CountryCode;
import synapps.resona.api.mysql.member.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_info_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @Column(name = "timezone")
    private Integer timezone;

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

    @Column(name="is_deleted")
    private boolean isDeleted = false;

    private PersonalInfo(Member member, Integer timezone, CountryCode nationality, CountryCode countryOfResidence, String phoneNumber, String birth, Gender gender, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.member = member;
        this.timezone = timezone;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.phoneNumber = phoneNumber;
        this.birth = parseToLocalDate(birth);
        this.age = birthToAge(parseToLocalDate(birth));
        this.gender = gender;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PersonalInfo of(Member member, Integer timezone, CountryCode nationality, CountryCode countryOfResidence, String phoneNumber, String birth, Gender gender, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new PersonalInfo(member, timezone, nationality, countryOfResidence, phoneNumber, birth, gender, location, createdAt, updatedAt);
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


    public void updatePersonalInfo(Integer timezone, CountryCode nationality, CountryCode countryOfResidence, String phoneNumber, String birth, Gender gender, String location) {
        this.timezone = timezone;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.phoneNumber = phoneNumber;
        this.birth = parseToLocalDate(birth);
        this.age = birthToAge(parseToLocalDate(birth));
        this.gender = gender;
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
    }

}
