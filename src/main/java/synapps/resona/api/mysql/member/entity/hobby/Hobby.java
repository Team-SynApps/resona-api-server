package synapps.resona.api.mysql.member.entity.hobby;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Hobby extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hobby_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_details_id")
  private MemberDetails memberDetails;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "given_hobby")
  private GivenHobby givenHobby;

  @Column(name = "hobby_name")
  private String name;

  private Hobby(MemberDetails memberDetails, GivenHobby givenHobby) {
    this.memberDetails = memberDetails;
    this.givenHobby = givenHobby;
    this.name = GivenHobby.NOT_GIVEN.getName();
  }

  private Hobby(MemberDetails memberDetails, String name) {
    this.memberDetails = memberDetails;
    this.givenHobby = GivenHobby.NOT_GIVEN;
    this.name = name;
  }

  public static Hobby of(MemberDetails memberDetails, String name) {
    return new Hobby(memberDetails, name);
  }

  public static Hobby of(MemberDetails memberDetails, GivenHobby givenHobby) {
    return new Hobby(memberDetails, givenHobby);
  }

  public void updateName(String name) {
    GivenHobby givenHobby = GivenHobby.of(name);
    if (givenHobby.equals(GivenHobby.NOT_GIVEN)) {
      this.name = name;
    } else {
      this.name = givenHobby.getName();
    }
  }
}
