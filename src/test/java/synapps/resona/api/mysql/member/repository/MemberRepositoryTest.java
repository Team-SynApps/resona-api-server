package synapps.resona.api.mysql.member.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;

@Transactional
class MemberRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private MemberRepository memberRepository;

  private Member testMember;

  @BeforeEach
  void beforeAll() {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011111111", MBTI.ENFJ, "test about me",
        "test location");
    Profile profile = Profile.of(
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "닉네임3",
        "http://profile.img/3",
        "2000-01-01"
    );
    testMember = Member.of(accountInfo, memberDetails, profile, "test@example.com", "password123",
        LocalDateTime.now());
    memberRepository.save(testMember);
  }

  @Test
  @DisplayName("멤버의 이메일로 프로필을 가져올 수 있다.")
  void findProfileByMemberEmail() {
    // given

    // when
    Optional<Profile> result = memberRepository.findProfileByEmail("test@example.com");

    // then
//        assertThat(result).isPresent();
    assertThat(result.get().getNickname()).isEqualTo("닉네임3"); // 원하는 필드로 비교
  }

  @Test
  @DisplayName("멤버의 이메일로 멤버 세부정보를 가져올 수 있다.")
  void findMemberDetailsByMemberEmail() {
    Optional<MemberDetails> result = memberRepository.findMemberDetailsByEmail("test@example.com");

//        assertThat(result).isPresent();
    assertThat(result.get().getAboutMe()).isEqualTo("test about me");

  }

}