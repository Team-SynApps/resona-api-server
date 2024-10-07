package synapps.resona.api.oauth.service;


import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.Sex;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
import synapps.resona.api.oauth.entity.UserPrincipal;
import synapps.resona.api.oauth.exception.OAuthProviderMissMatchException;
import synapps.resona.api.oauth.info.OAuth2UserInfo;
import synapps.resona.api.oauth.info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
//@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
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

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Member savedMember = memberRepository.findByEmail(userInfo.getEmail()).orElseThrow(MemberException::memberNotFound);

        if (savedMember != null) {
            if (providerType != savedMember.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedMember.getProviderType() + " account to login."
                );
            }
            updateMember(savedMember, userInfo);
        } else {
            savedMember = createMember(userInfo, providerType);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        Member member = Member.of(
                userInfo.getName(),                   // nickname
                null,                                 // phoneNumber (OAuth2에서 제공하지 않음)
                0,                                    // timezone (기본값 설정 필요)
                null,                                 // birth (OAuth2에서 제공하지 않음)
                null,                                 // comment (OAuth2에서 제공하지 않음)
                Sex.of("NO"),                    // sex (OAuth2에서 제공하지 않음)
                false,                                // isOnline
                userInfo.getEmail(),                  // email
                "",                                   // password (OAuth2 로그인이므로 빈 문자열)
                null,                                 // location (OAuth2에서 제공하지 않음)
                providerType,
                RoleType.USER,
                now,                                  // createdAt
                now,                                  // modifiedAt
                now                                   // lastAccessedAt
        );

        return memberRepository.saveAndFlush(member);
    }

    private Member updateMember(Member member, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !member.getNickname().equals(userInfo.getName())) {
            member.setUserNickname(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !member.getProfileImageUrl().equals(userInfo.getImageUrl())) {
            member.setProfileImageUrl(userInfo.getImageUrl());
        }

        return member;
    }
}