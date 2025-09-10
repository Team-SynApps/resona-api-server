package synapps.resona.api.member.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MBTI;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.repository.member_details.MemberDetailsRepository;

@Transactional
@DataJpaTest
@Import(TestQueryDslConfig.class)
class MemberDetailsRepositoryTest {

  @Autowired
  private MemberDetailsRepository memberDetailsRepository;

  @Autowired
  private MemberRepository memberRepository;

  private Member testMember;

  @BeforeEach
  void beforeAll() {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011111111", MBTI.ENFJ, "test about me",
        "test location");
    Profile profile = Profile.empty();
    testMember = Member.of(accountInfo, memberDetails, profile, "test@example.com", "password123",
        LocalDateTime.now());
    memberRepository.save(testMember);
  }

}