package com.synapps.resona.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.Profile;
import fixture.MemberFixture;
import com.synapps.resona.repository.member.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
@Disabled
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  private Member testMember;

  @BeforeEach
  void setUp() {
    testMember = MemberFixture.createMemberForRepositoryTest("test@example.com");
    memberRepository.save(testMember);
  }

  @Test
  @DisplayName("멤버의 이메일로 프로필을 가져올 수 있다.")
  void findProfileByMemberEmail() {
    // when
    Optional<Profile> result = memberRepository.findProfileByEmail("test@example.com");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getNickname()).isEqualTo("닉네임3");
  }

  @Test
  @DisplayName("멤버의 이메일로 멤버 세부정보를 가져올 수 있다.")
  void findMemberDetailsByMemberEmail() {
    // when
    Optional<MemberDetails> result = memberRepository.findMemberDetailsByEmail("test@example.com");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getAboutMe()).isEqualTo("test about me");
  }
}