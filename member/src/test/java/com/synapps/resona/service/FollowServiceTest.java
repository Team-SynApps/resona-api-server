package com.synapps.resona.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import fixture.MemberFixture;
import com.synapps.resona.repository.member.FollowRepository;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.dto.request.auth.RegisterRequest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

  @InjectMocks
  private FollowService followService;
  @Mock
  private FollowRepository followRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private MemberService memberService;

  @Mock
  private TempTokenService tempTokenService;

  private Long meId;
  private Long targetId;
  private final RegisterRequest meReq = MemberFixture.createMeRegisterRequest();
  private final RegisterRequest targetReq = MemberFixture.createTargetRegisterRequest();

//  @BeforeEach
//  void setUp() {
//    Member me = MemberFixture.createMe();
//    Member target = MemberFixture.createTarget();
//
//    meId = me.getId();
//    targetId = target.getId();
//
//    when(memberRepository.findByEmail(meReq.getEmail())).thenReturn(Optional.of(me));
//    when(memberRepository.findByEmail(targetReq.getEmail())).thenReturn(Optional.of(target));
//    when(memberRepository.findByEmailWithAccountInfo(meReq.getEmail())).thenReturn(Optional.of(me));
//
//    UserPrincipal principal = UserPrincipal.create(me);
//
//    SecurityContextHolder.getContext().setAuthentication(
//        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
//  }

//  @Test
//  @DisplayName("follow를 성공한다")
//  void follow_success() {
//    // given
//    Member me = MemberFixture.createMe();
//    Member target = MemberFixture.createTarget();
//    when(memberRepository.findById(meId)).thenReturn(Optional.of(me));
//    when(memberRepository.findById(targetId)).thenReturn(Optional.of(target));
//    when(followRepository.existsByFollowerAndFollowing(any(), any())).thenReturn(false);
//    when(followRepository.save(any())).thenReturn(null);
//
//    // when
//    followService.follow(targetId);
//
//    // then
//    verify(followRepository, times(1)).save(any());
//  }
//
//  @Test
//  @DisplayName("unfollow를 성공한다")
//  void unfollow_success() {
//    // given
//    Member me = MemberFixture.createMe();
//    Member target = MemberFixture.createTarget();
//    Follow follow = new Follow(me, target);
//
//    when(memberRepository.findById(meId)).thenReturn(Optional.of(me));
//    when(memberRepository.findById(targetId)).thenReturn(Optional.of(target));
//    when(followRepository.findByFollowerAndFollowing(me, target)).thenReturn(Optional.of(follow));
//    doNothing().when(followRepository).delete(any(Follow.class));
//
//    // when
//    followService.unfollow(targetId);
//
//    // then
//    verify(followRepository, times(1)).delete(any(Follow.class));
//  }

//  @Test
//  @DisplayName("팔로우한 목록을 조회할 수 있다")
//  void getFollowings_success() {
//    // given
//    Member me = MemberFixture.createMe();
//    Member target = MemberFixture.createTarget();
//    Follow follow = new Follow(me, target);
//
//    when(memberRepository.findById(meId)).thenReturn(Optional.of(me));
//    when(followRepository.findAllByFollower(me)).thenReturn(List.of(follow));
//    when(memberRepository.findById(target.getId())).thenReturn(Optional.of(target));
//
//    // when
//    List<MemberProfileDto> result = followService.getFollowings(meId);
//
//    // then
//    assertThat(result).hasSize(1);
//    assertThat(result.get(0).getNickname()).isEqualTo(target.getNickname());
//  }

//  @Test
//  @DisplayName("팔로워 목록을 조회할 수 있다")
//  void getFollowers_success() {
//    // given
//    Member me = MemberFixture.createMe();
//    Member another = MemberFixture.createAnother();
//    Follow follow = new Follow(another, me);
//
//    when(memberRepository.findById(meId)).thenReturn(Optional.of(me));
//    when(followRepository.findAllByFollowing(me)).thenReturn(List.of(follow));
//    when(memberRepository.findById(another.getId())).thenReturn(Optional.of(another));
//
//    // when
//    List<MemberProfileDto> result = followService.getFollowers(meId);
//
//    // then
//    assertThat(result).hasSize(1);
//    assertThat(result.get(0).getNickname()).isEqualTo(another.getNickname());
//  }
}
