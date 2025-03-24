package synapps.resona.api.mysql.member.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class MemberDetailsRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private MemberDetailsRepository memberDetailsRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void beforeAll() {
        testMember = Member.of("test@example.com", "password123", LocalDateTime.now());
        memberRepository.save(testMember);
    }

    @Test
    @DisplayName("멤버를 기준으로 멤버 상세 정보를 조회한다.")
    public void findByMember() {
        // given
        MemberDetails details = MemberDetails.of(testMember, 0, "01011111111", MBTI.ENFJ, "test about me", "test location");
        memberDetailsRepository.save(details);

        // when
        Optional<MemberDetails> detailsFound = memberDetailsRepository.findByMember(testMember);

        // then
        assertThat(detailsFound).isPresent();
        assertThat(detailsFound.get().getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("멤버 id를 기준으로 멤버 상세 정보를 조회한다.")
    void findByMemberId() {
        // given
        MemberDetails details = MemberDetails.of(testMember, 0, "01011111111", MBTI.ENFJ, "test about me", "test location");
        memberDetailsRepository.save(details);

        // when
        Optional<MemberDetails> detailsFound = memberDetailsRepository.findByMemberId(testMember.getId());

        // then
        assertThat(detailsFound).isPresent();
        assertThat(detailsFound.get().getMember()).isEqualTo(testMember);
    }
}