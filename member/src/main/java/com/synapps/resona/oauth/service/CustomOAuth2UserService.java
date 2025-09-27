package com.synapps.resona.oauth.service;

import com.synapps.resona.entity.account.AccountInfo;
import com.synapps.resona.entity.account.AccountStatus;
import com.synapps.resona.entity.account.RoleType;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.member.MemberProvider;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.entity.profile.Profile;
import com.synapps.resona.entity.account.ProviderType;
import com.synapps.resona.entity.member.UserPrincipal;
import com.synapps.resona.oauth.info.OAuth2UserInfo;
import com.synapps.resona.oauth.info.OAuth2UserInfoFactory;
import com.synapps.resona.repository.member.MemberProviderRepository;
import com.synapps.resona.repository.member.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final MemberProviderRepository memberProviderRepository; // Repository 주입

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    try {
      return this.process(userRequest, user);
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
    ProviderType providerType = ProviderType.valueOf(
        userRequest.getClientRegistration().getRegistrationId().toUpperCase()
    );

    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
        providerType,
        user.getAttributes()
    );

    // 이메일로 기존 회원 조회
    Member savedMember = memberRepository.findByEmail(userInfo.getEmail())
        .orElse(null);

    if (savedMember != null) {
      // 기존 회원이 존재하면, 현재 소셜 제공자 정보가 이미 연결되어 있는지 확인
      Member finalSavedMember = savedMember;
      memberProviderRepository.findByMemberAndProviderType(savedMember, providerType)
          .orElseGet(() -> {
            // 연결되어 있지 않다면 새로 연결
            MemberProvider newProvider = MemberProvider.of(finalSavedMember, providerType, userInfo.getId());
            return memberProviderRepository.save(newProvider);
          });
    } else {
      // 신규 회원이면 Member와 관련 엔티티들 생성
      savedMember = createMember(userInfo, providerType);
    }

    // UserPrincipal 생성 시 providerType을 직접 전달
    return UserPrincipal.create(savedMember, providerType, user.getAttributes());
  }

  private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
    LocalDateTime now = LocalDateTime.now();

    // AccountInfo 생성
    AccountInfo newAccountInfo = AccountInfo.of(
        RoleType.USER,
        AccountStatus.TEMPORARY
    );
    Profile newProfile = Profile.empty();
    MemberDetails newMemberDetails = MemberDetails.empty();

    Member newMember = Member.of(
        newAccountInfo,
        newMemberDetails,
        newProfile,
        userInfo.getEmail(),
        "", // 소셜 로그인은 비밀번호 없음
        LocalDateTime.now()
    );


    // MemberProvider 생성 및 Member에 연결
    MemberProvider newProvider = MemberProvider.of(newMember, providerType, userInfo.getId());
    newMember.addProvider(newProvider);

    return memberRepository.saveAndFlush(newMember);
  }
}