package com.synapps.resona.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.synapps.resona.dto.response.TempTokenResponse;
import com.synapps.resona.entity.account.AccountInfo;
import com.synapps.resona.entity.account.AccountStatus;
import com.synapps.resona.entity.account.RoleType;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.Profile;
import com.synapps.resona.entity.token.AuthTokenProvider;
import com.synapps.resona.properties.AppProperties;
import com.synapps.resona.repository.account.AccountInfoRepository;
import com.synapps.resona.repository.member.MemberRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
class TempTokenServiceTest {

  @InjectMocks
  private TempTokenService tempTokenService;

  @Mock
  private AuthTokenProvider tokenProvider;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private AccountInfoRepository accountInfoRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private AppProperties appProperties;

  private String testEmail;
  private Member testMember;
  private AccountInfo testAccountInfo;

  @BeforeEach
  void setUp() {
    testEmail = "test@example.com";
    testAccountInfo = AccountInfo.of(RoleType.GUEST, AccountStatus.TEMPORARY);
    MemberDetails emptyMemberDetails = MemberDetails.empty();
    Profile emptyProfile = Profile.empty();
    testMember = Member.of(testAccountInfo, emptyMemberDetails, emptyProfile, testEmail,
        "password123", LocalDateTime.now());
  }

//  @Test
//  @DisplayName("임시 토큰을 정상적으로 발급한다.")
//  void createTemporaryToken() {
//    // Given
//    when(memberRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
//    when(memberRepository.save(any(Member.class))).thenReturn(testMember);
//    when(accountInfoRepository.save(any(AccountInfo.class))).thenReturn(testAccountInfo);
//    when(tokenProvider.createAuthToken(anyString(), any()).getToken().
//    when(tokenProvider.createRefreshToken(anyString(), anyList(), anyBoolean())).thenReturn("dummyRefreshToken");
//
//    // When
//    TempTokenResponse response = tempTokenService.createTemporaryToken(testEmail);
//
//    // Then
//    assertThat(response).isNotNull();
//    assertThat(response.getAccessToken()).isEqualTo("dummyAccessToken");
//    assertThat(response.getRefreshToken()).isEqualTo("dummyRefreshToken");
//    assertThat(response.isRegistered()).isFalse();
//
//    verify(memberRepository, times(1)).findByEmail(testEmail);
//    verify(memberRepository, times(1)).save(any(Member.class));
//    verify(accountInfoRepository, times(1)).save(any(AccountInfo.class));
//    verify(tokenProvider, times(1)).createToken(anyString(), anyList(), anyBoolean());
//    verify(tokenProvider, times(1)).createRefreshToken(anyString(), anyList(), anyBoolean());
//  }
}