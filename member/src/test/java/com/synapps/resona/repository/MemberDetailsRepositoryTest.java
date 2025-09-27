package com.synapps.resona.repository;

import com.synapps.resona.entity.account.AccountInfo;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.member_details.MBTI;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.Profile;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.repository.member_details.MemberDetailsRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import support.RepositoryTestSupport;

@Transactional
class MemberDetailsRepositoryTest extends RepositoryTestSupport {

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