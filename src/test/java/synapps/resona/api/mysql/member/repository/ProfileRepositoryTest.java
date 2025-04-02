package synapps.resona.api.mysql.member.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProfileRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버의 이메일로 프로필을 가져올 수 있다.")
    void findByMemberEmail() {
        Member member = Member.of("fetch@example.com", "password", LocalDateTime.now());
        memberRepository.save(member);

        Profile profile = Profile.of(
                member,
                CountryCode.KR,
                CountryCode.KR,
                Set.of(Language.KOREAN),
                Set.of(Language.ENGLISH),
                "닉네임3",
                "http://profile.img/3",
                "2000-01-01"
        );
        profileRepository.save(profile);

        Optional<Profile> result = profileRepository.findByMemberEmail("fetch@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getMember().getEmail()).isEqualTo("fetch@example.com");
    }
}