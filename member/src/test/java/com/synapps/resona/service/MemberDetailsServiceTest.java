package com.synapps.resona.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.synapps.resona.repository.member.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberDetailsServiceTest {

  private final String email = "test@resona.com";

  @Mock
  private MemberService memberService;

  @InjectMocks
  private MemberDetailsService memberDetailsService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private TempTokenService tempTokenService;
//
//  @BeforeEach
//  void setUp() {
//    Member member = MemberFixture.createDetailsTestMember(email);
//    when(memberRepository.findByEmailWithAccountInfo(email)).thenReturn(Optional.of(member));
//
//    UserPrincipal principal = UserPrincipal.create(member);
//
//    UsernamePasswordAuthenticationToken authentication =
//        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//    SecurityContextHolder.getContext().setAuthentication(authentication);
//  }
//
//  @Test
//  @DisplayName("회원 상세정보를 등록 및 수정할 수 있다.")
//  void register() {
//    // given
//    MemberDetailsRequest request = MemberFixture.createDetailsRegisterRequest();
//    Member member = MemberFixture.createDetailsTestMember(email);
//    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
//    when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//    // when
//    MemberDetailsResponse result = memberDetailsService.register(request);
//
//    // then
//    assertThat(result.getTimezone()).isEqualTo(9);
//    assertThat(result.getMbti()).isEqualTo(MBTI.ENFJ);
//    assertThat(result.getAboutMe()).isEqualTo("자기소개입니다");
//    assertThat(result.getLocation()).isEqualTo("서울 강남구");
//  }
//
//  @Test
//  @DisplayName("회원 상세정보를 조회할 수 있다.")
//  void getMemberDetails() {
//    // given
//    Member member = MemberFixture.createDetailsTestMember(email);
//    member.registerDetails(MemberFixture.createDetailsReadRequest().toEntity());
//    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
//
//    // when
//    MemberDetailsResponse result = memberDetailsService.getMemberDetails();
//
//    // then
//    assertThat(result.getTimezone()).isEqualTo(8);
//    assertThat(result.getMbti()).isEqualTo(MBTI.INFP);
//    assertThat(result.getLocation()).isEqualTo("부산 해운대구");
//  }
//
//  @Test
//  @DisplayName("회원 상세정보를 수정할 수 있다.")
//  void editMemberDetails() {
//    // given
//    Member member = MemberFixture.createDetailsTestMember(email);
//    member.registerDetails(MemberFixture.createDetailsInitialRequest().toEntity());
//    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
//    when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//    MemberDetailsRequest updateRequest = MemberFixture.createDetailsUpdateRequest();
//
//    // when
//    var updated = memberDetailsService.editMemberDetails(updateRequest);
//
//    // then
//    assertThat(updated.getTimezone()).isEqualTo(7);
//    assertThat(updated.getMbti()).isEqualTo(MBTI.ENTP);
//    assertThat(updated.getAboutMe()).isEqualTo("수정된 소개");
//    assertThat(updated.getLocation()).isEqualTo("제주도 제주시");
//  }
//
//  @Test
//  @DisplayName("회원 상세정보를 soft delete 할 수 있다.")
//  void deleteMemberDetails() {
//    // given
//    Member member = MemberFixture.createDetailsTestMember(email);
//    member.registerDetails(MemberFixture.createDetailsDeleteRequest().toEntity());
//    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
//    when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//    // when
//    var deleted = memberDetailsService.deleteMemberDetails();
//
//    // then
//    assertThat(deleted.isDeleted()).isTrue();
//  }
}
