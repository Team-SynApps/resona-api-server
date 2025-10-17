package com.synapps.resona.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.command.dto.request.auth.RegisterRequest;
import com.synapps.resona.command.dto.response.MemberRegisterResponseDto;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.UserPrincipal;
import fixture.MemberFixture;
import com.synapps.resona.command.repository.member.MemberProviderRepository;
import com.synapps.resona.command.repository.member.MemberRepository;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@ExtendWith(MockitoExtension.class)
@Disabled
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberProviderRepository memberProviderRepository;

//  @BeforeEach
//  void setUp() {
//    testMember = MemberFixture.createTestMember();
//    guestMember = MemberFixture.createGuestMember();
//
//    testMember.encodePassword("password1234");
//
//    memberRepository.saveAll(List.of(testMember, guestMember));
//    // SecurityContext에 사용자 정보 설정
//    setAuthentication(testMember.getEmail());
//  }

  private void setAuthentication(Member member) {
    UserPrincipal principal = UserPrincipal.create(member);

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        principal, null, principal.getAuthorities());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(auth);
  }

  @Test
  @DisplayName("회원 상세 정보를 조회한다.")
  void testGetMemberDetailInfo() {
    // given
    Member testMember = MemberFixture.createTestMember();
    setAuthentication(testMember);
    when(memberRepository.findByEmailWithAccountInfo(anyString())).thenReturn(Optional.of(testMember));

    // when
    var memberDetailInfo = memberService.getMemberDetailInfo("test1@example.com");

    // then
    assertThat(memberDetailInfo).isNotNull();
    assertThat(memberDetailInfo.getRoleType()).isEqualTo("USER");
  }

  @Test
  @DisplayName("회원 가입을 한다.")
  void testSignUp() throws Exception {
    // given
    RegisterRequest request = MemberFixture.createNewUserRegisterRequest();
    when(memberRepository.existsByEmail(anyString())).thenReturn(false);
    when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
      Member member = invocation.getArgument(0);
      return member;
    });

    // when
    MemberRegisterResponseDto newMember = memberService.signUp(request);

    // then
    assertThat(newMember).isNotNull();
    assertThat(newMember.getEmail()).isEqualTo("newuser1@example.com");
    verify(memberRepository, times(1)).existsByEmail(anyString());
    verify(memberRepository, times(1)).save(any(Member.class));
  }

  @Test
  @DisplayName("회원을 삭제한다.")
  void testDeleteUser() {
    // given
    Member memberToDelete = MemberFixture.createTestMember();
    setAuthentication(memberToDelete);
    when(memberRepository.findByEmailWithAccountInfo(anyString())).thenReturn(Optional.of(memberToDelete));
    doNothing().when(memberRepository).delete(any(Member.class));
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // when
    Map<String, String> result = memberService.deleteUser();

    // then
    assertThat(result).isEqualTo(Map.of("message", "User deleted successfully."));
    verify(memberRepository, times(1)).delete(any(Member.class));
    verify(memberRepository, times(1)).findByEmail(anyString());
  }

//  @Test
//  @DisplayName("회원가입 시 MemberUpdatedEvent가 발행되고, MongoDB에 ChatMember가 동기화되어야 한다.")
//  void signUp_ShouldSyncToChatMemberInMongoDB() {
//    // given
//    RegisterRequest request = MemberFixture.createMongoSyncRegisterRequest();
//
//    // when
//    MemberRegisterResponseDto newMember = memberService.signUp(request);
//
//    // then
//    assertThat(newMember).isNotNull();
//    assertThat(newMember.getEmail()).isEqualTo("newuser1@example.com");
//
//    ChatMember chatMember = chatMemberRepository.findById(newMember.getMemberId()).orElse(null);
//
//    assertThat(chatMember).isNotNull();
//    assertThat(chatMember.getId()).isEqualTo(newMember.getMemberId());
//    assertThat(chatMember.getNickname()).isEqualTo("MongoDB동기화테스트");
//    assertThat(chatMember.getProfileImageUrl()).isEqualTo("http://example.com/profile.jpg");
//  }
}
