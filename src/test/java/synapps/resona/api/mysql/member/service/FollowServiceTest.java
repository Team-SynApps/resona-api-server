package synapps.resona.api.mysql.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.response.MemberProfileDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.member.entity.profile.Language;
import synapps.resona.api.member.repository.member.FollowRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.service.FollowService;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.member.service.TempTokenService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Transactional
class FollowServiceTest extends IntegrationTestSupport {

  private final String loginEmail = "me@resona.com";
  private final String targetEmail = "target@resona.com";

  @Autowired
  private FollowService followService;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private MemberService memberService;

  @Autowired
  private TempTokenService tempTokenService;

  private Long meId;
  private Long targetId;

  @BeforeEach
  void setUp() {
    RegisterRequest meReq = new RegisterRequest(
        loginEmail, "mine tag", "secure123!", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 9, "1998-07-21",
        "나자신", "http://image.me"
    );
    RegisterRequest otherReq = new RegisterRequest(
        targetEmail, "other tag", "secure123!", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 7, "1995-01-01",
        "상대방", "http://image.you"
    );

    // 1. 임시 회원을 먼저 생성
    tempTokenService.createTemporaryToken(meReq.getEmail());
    tempTokenService.createTemporaryToken(otherReq.getEmail());

    // 2. 정식 회원으로 전환
    memberService.signUp(meReq);
    memberService.signUp(otherReq);

    // 3. 테스트에 사용할 ID 조회
    meId = memberRepository.findByEmail(loginEmail).orElseThrow().getId();
    targetId = memberRepository.findByEmail(targetEmail).orElseThrow().getId();

    // 4. 로그인된 사용자 인증 정보 설정 (UserPrincipal 사용)
    Member me = memberRepository.findByEmailWithAccountInfo(loginEmail)
        .orElseThrow(() -> new RuntimeException("테스트 유저 'me'를 찾을 수 없습니다."));

    UserPrincipal principal = UserPrincipal.create(me);

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  @Test
  @DisplayName("follow를 성공한다")
  void follow_success() {
    // when
    followService.follow(targetId);

    // then
    assertThat(followRepository.existsByFollowerAndFollowing(
        memberRepository.findById(meId).get(),
        memberRepository.findById(targetId).get())
    ).isTrue();
  }

  @Test
  @DisplayName("unfollow를 성공한다")
  void unfollow_success() {
    // given
    followService.follow(targetId); // 먼저 팔로우하고

    // when
    followService.unfollow(targetId);

    // then
    assertThat(followRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("팔로우한 목록을 조회할 수 있다")
  void getFollowings_success() {
    // given
    followService.follow(targetId);

    // when
    List<MemberProfileDto> result = followService.getFollowings(meId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNickname()).isEqualTo("상대방");
  }

  @Test
  @DisplayName("팔로워 목록을 조회할 수 있다")
  void getFollowers_success() {
    // given
    String thirdEmail = "another@resona.com";
    RegisterRequest anotherReq = new RegisterRequest(
        thirdEmail, "another tag", "pw", CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH), 1, "2000-01-01",
        "제3자", "http://img.third"
    );

    tempTokenService.createTemporaryToken(anotherReq.getEmail());
    memberService.signUp(anotherReq);

    Member third = memberRepository.findByEmailWithAccountInfo(thirdEmail)
        .orElseThrow(() -> new RuntimeException("테스트 유저 'third'를 찾을 수 없습니다."));

    UserPrincipal thirdPrincipal = UserPrincipal.create(third);

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(thirdPrincipal, null, thirdPrincipal.getAuthorities())
    );
    followService.follow(meId); // 제3자가 나(meId)를 팔로우

    // when
    List<MemberProfileDto> result = followService.getFollowers(meId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNickname()).isEqualTo("제3자");
  }
}