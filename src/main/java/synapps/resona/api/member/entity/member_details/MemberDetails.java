package synapps.resona.api.member.entity.member_details;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.member.entity.hobby.Hobby;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
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
