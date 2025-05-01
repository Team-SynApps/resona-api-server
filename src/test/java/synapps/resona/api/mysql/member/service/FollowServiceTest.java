package synapps.resona.api.mysql.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.response.MemberProfileDto;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.repository.FollowRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

@Transactional
class FollowServiceTest extends IntegrationTestSupport {

  private final String loginEmail = "me@resona.com";
  @Autowired
  private FollowService followService;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private MemberService memberService;
  private Long meId;
  private Long targetId;

  @BeforeEach
  void setUp() {
    // 로그인한 사용자 등록
    RegisterRequest me = new RegisterRequest(
        loginEmail,
        "secure123!",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        9,
        "1998-07-21",
        "나자신",
        "http://image.me"
    );

    memberService.signUp(me);

    // 상대 유저 등록
    RegisterRequest other = new RegisterRequest(
        "target@resona.com",
        "secure123!",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        7,
        "1995-01-01",
        "상대방",
        "http://image.you"
    );
    memberService.signUp(other);

    meId = memberRepository.findByEmail(loginEmail).orElseThrow().getId();
    targetId = memberRepository.findByEmail("target@resona.com").orElseThrow().getId();

    // 로그인된 사용자 인증 정보 설정
    User principal = new User(loginEmail, "", new ArrayList<>());
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  @Test
  @DisplayName("follow를 성공한다")
  void follow_success() {
    followService.follow(targetId);

    assertThat(followRepository.existsByFollowerAndFollowing(
        memberRepository.findById(meId).get(),
        memberRepository.findById(targetId).get())
    ).isTrue();
  }

  @Test
  @DisplayName("unfollow를 성공한다")
  void unfollow_success() {
    followService.follow(targetId); // 먼저 팔로우하고

    followService.unfollow(targetId);

    assertThat(followRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("팔로우한 목록을 조회할 수 있다")
  void getFollowings_success() {
    followService.follow(targetId);

    List<MemberProfileDto> result = followService.getFollowings(meId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNickname()).isEqualTo("상대방");
  }

  @Test
  @DisplayName("팔로워 목록을 조회할 수 있다")
  void getFollowers_success() {
    // 상대방이 나를 팔로우한 상황 만들기
    // (이 경우 상대방을 로그인 상태로 설정 후 follow 호출)
    RegisterRequest other = new RegisterRequest(
        "another@resona.com",
        "pw",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        1,
        "2000-01-01",
        "제3자",
        "http://img.third"
    );
    memberService.signUp(other);

    Long thirdId = memberRepository.findByEmail("another@resona.com").orElseThrow().getId();

    // 로그인 교체
    User third = new User("another@resona.com", "", new ArrayList<>());
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(third, null, third.getAuthorities()));

    followService.follow(meId); // 제3자가 나(meId)를 팔로우

    List<MemberProfileDto> result = followService.getFollowers(meId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNickname()).isEqualTo("제3자");
  }
}
