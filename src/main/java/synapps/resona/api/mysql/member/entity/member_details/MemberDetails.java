package synapps.resona.api.mysql.member.entity.member_details;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_info_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @Column(name = "timezone")
    private Integer timezone;


    @Size(max = 20)
    @Column(name = "phone_number")
    @NotNull
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "MBTI")
    @Nullable
    private MBTI mbti;

    @Column(name = "about_me")
    private String aboutMe;

    @Nullable
    @Column(name = "location")
    private String location;

    private MemberDetails(Member member,
                          Integer timezone,
                          String phoneNumber,
                          MBTI mbti,
                          String aboutMe,
                          String location) {
        this.member = member;
        this.timezone = timezone;
        this.phoneNumber = phoneNumber;
        this.mbti = mbti;
        this.aboutMe = aboutMe;
        this.location = location;
    }

    private MemberDetails(Member member,
                          Integer timezone) {
        this.member = member;
        this.timezone = timezone;
        this.phoneNumber = "";
        this.mbti = MBTI.NONE;
        this.aboutMe = "";
        this.location = "";
    }

    public static MemberDetails of(Member member,
                                   Integer timezone,
                                   String phoneNumber,
                                   MBTI mbti,
                                   String aboutMe,
                                   String location) {
        return new MemberDetails(member, timezone, phoneNumber, mbti, aboutMe, location);
    }

    // when registered
    public static MemberDetails of(Member member,
                                   Integer timezone) {
        return new MemberDetails(member, timezone);
    }

    public void updatePersonalInfo(Integer timezone,
                                   String phoneNumber,
                                   MBTI mbti,
                                   String aboutMe,
                                   String location) {
        this.timezone = timezone;
        this.phoneNumber = phoneNumber;
        this.mbti = mbti;
        this.aboutMe = aboutMe;
        this.location = location;
    }
}
