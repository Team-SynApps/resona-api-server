package com.synapps.resona.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.command.service.ProfileService;
import com.synapps.resona.command.dto.MemberDto;
import com.synapps.resona.command.dto.request.profile.ProfileRequest;
import com.synapps.resona.command.dto.response.ProfileResponse;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.UserPrincipal;
import fixture.MemberFixture;
import com.synapps.resona.command.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Disabled
class ProfileServiceTest {

  private final String email = "test@resona.com";

  private MemberDto memberInfo;
  @Mock
  private MemberService memberService;
  @Mock
  private MemberRepository memberRepository;
  @InjectMocks
  private ProfileService profileService;

  @BeforeEach
  void setUp() {
    Member newMember = MemberFixture.createProfileTestMember(email);
    newMember.encodePassword(newMember.getPassword());

    when(memberRepository.save(any(Member.class))).thenReturn(newMember);
//    when(memberService.signUp(any(RegisterRequest.class))).thenReturn(new MemberRegisterResponseDto(newMember.getId(), newMember.getEmail()));
    when(memberRepository.findByEmailWithAccountInfo(email)).thenReturn(Optional.of(newMember));

    UserPrincipal principal = UserPrincipal.create(newMember);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    memberInfo = MemberFixture.createTestMemberDto(newMember.getId(), email);
  }

  @Test
  @DisplayName("프로필 등록에 성공한다.")
  void register() {
    // given
    ProfileRequest request = MemberFixture.createProfileRegisterRequest();
    Member member = MemberFixture.createProfileTestMember(email);
    when(memberRepository.findById(memberInfo.getId())).thenReturn(Optional.of(member));
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    // when
    ProfileResponse result = profileService.register(request, memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("등록된닉네임");
    assertThat(result.getNativeLanguageCodes()).containsExactly("ko");
    assertThat(result.getInterestingLanguageCodes()).contains("en");
    assertThat(result.getGender()).isEqualTo("MAN");
    assertThat(result.getComment()).isEqualTo("등록 테스트용 자기소개입니다.");
  }

  @Test
  @DisplayName("등록된 프로필을 조회할 수 있다.")
  void readProfile() {
    // given
    Member member = MemberFixture.createProfileTestMember(email);
//    member.registerProfile(MemberFixture.createProfileReadRequest().toEntity());
    when(memberRepository.findById(memberInfo.getId())).thenReturn(Optional.of(member));

    // when
    ProfileResponse result = profileService.readProfile(memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("조회용닉네임");
    assertThat(result.getGender()).isEqualTo("WOMAN");
    assertThat(result.getComment()).isEqualTo("조회 테스트용 자기소개");
  }

  @Test
  @DisplayName("기존 프로필을 수정할 수 있다.")
  void editProfile() {
    // given
    Member member = MemberFixture.createProfileTestMember(email);
//    member.registerProfile(MemberFixture.createProfileReadRequest().toEntity());
    when(memberRepository.findById(memberInfo.getId())).thenReturn(Optional.of(member));
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    ProfileRequest updateRequest = MemberFixture.createProfileUpdateRequest();

    // when
    ProfileResponse result = profileService.editProfile(updateRequest, memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("수정된닉네임");
    assertThat(result.getNationality()).isEqualTo("JP");
    assertThat(result.getCountryOfResidence()).isEqualTo("US");
    assertThat(result.getNativeLanguageCodes()).containsExactly("ja");
    assertThat(result.getInterestingLanguageCodes()).contains("en", "fr");
    assertThat(result.getGender()).isEqualTo("MAN");
    assertThat(result.getComment()).isEqualTo("수정된 소개입니다.");
  }

  @Test
  @DisplayName("프로필 삭제 시 softDelete 되어야 하며, 더 이상 조회되지 않아야 한다.")
  void deleteProfile() {
    // given
    Member member = MemberFixture.createProfileTestMember(email);
//    member.registerProfile(MemberFixture.createProfileReadRequest().toEntity());
    when(memberRepository.findById(memberInfo.getId())).thenReturn(Optional.of(member));
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    // when
    profileService.deleteProfile(memberInfo);

    // then
    assertThat(member.getProfile().isDeleted()).isTrue();
  }

  @Test
  @DisplayName("이미 등록된 tag 값이 있는 경우 true를 반환한다.")
  void checkDuplicateTag_shouldReturnTrueWhenTagExists() {
    // given
    String existingTag = "test_tag";
//    when(memberRepository.existsByProfileTag(existingTag)).thenReturn(true);

    // when
    boolean result = profileService.checkDuplicateTag(existingTag);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("등록되지 않은 tag 값일 경우 false를 반환한다.")
  void checkDuplicateTag_shouldReturnFalseWhenTagDoesNotExist() {
    // given
    String nonExistentTag = "non-existent-tag";
//    when(memberRepository.exis(nonExistentTag)).thenReturn(false);

    // when
    boolean result = profileService.checkDuplicateTag(nonExistentTag);

    // then
    assertThat(result).isFalse();
  }
}
