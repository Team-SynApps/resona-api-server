package synapps.resona.api.mysql.member.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class MemberRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원이 존재하는지 확인한다.")
    void existsByEmail() {
        // given
        Member member = Member.of("test@example.com", "password123", LocalDateTime.now());
        memberRepository.save(member);

        // when
        Boolean exists = memberRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일로 회원을 조회한다.")
    void findByEmail() {
        // given
        Member member = Member.of("user@example.com", "securepassword", LocalDateTime.now());
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByEmail("user@example.com");

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("ID로 회원을 조회한다.")
    void findById() {
        // given
        Member member = Member.of("member@test.com", "password", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId());
    }
}