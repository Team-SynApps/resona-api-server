package synapps.resona.api.mysql.member.entity.member_details;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.hobby.Hobby;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Mention;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_details_id")
    private Long id;

    @OneToMany(mappedBy = "memberDetails")
    private final List<Hobby> hobbies = new ArrayList<>();

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

    private MemberDetails(Integer timezone,
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

    private MemberDetails(Integer timezone) {
        this.timezone = timezone;
        this.phoneNumber = "";
        this.mbti = MBTI.NONE;
        this.aboutMe = "";
        this.location = "";
    }


    public static MemberDetails of(Integer timezone,
                                   String phoneNumber,
                                   MBTI mbti,
                                   String aboutMe,
                                   String location) {
        return new MemberDetails(timezone, phoneNumber, mbti, aboutMe, location);
    }

    public static MemberDetails empty() {
        return new MemberDetails(
                0,
                "",
                MBTI.NONE,
                "",
                ""
        );
    }

    // when registered
    public static MemberDetails of(Integer timezone) {
        return new MemberDetails(timezone);
    }
  
    public void modifyMemberDetails(Integer timezone,
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

    public void join(Integer timezone) {
        this.timezone = timezone;
    }
}
