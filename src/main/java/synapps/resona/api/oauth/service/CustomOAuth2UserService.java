package synapps.resona.api.oauth.service;


import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
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
import synapps.resona.api.oauth.token.AuthTokenProvider;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final AuthTokenProvider tokenProvider;

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

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                providerType,
                user.getAttributes()
        );

        Member savedMember = memberRepository.findByEmail(userInfo.getEmail()).orElse(null);
        if (savedMember == null) {
            savedMember = createMember(userInfo, providerType);
        }

        AccountInfo accountInfo = accountInfoRepository.findByMember(savedMember);


        if (providerType != accountInfo.getProviderType()) {
            throw new OAuthProviderMissMatchException(
                    "Looks like you're signed up with " + providerType +
                            " account. Please use your " + accountInfo.getProviderType() + " account to login."
            );
        }

        return UserPrincipal.create(savedMember, accountInfo, user.getAttributes());
    }

    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();

        // Apple 사용자의 이름이 없을 수 있으므로 기본 이름 설정
        String nickname = userInfo.getName() != null ? userInfo.getName() : "User" + now.getNano();

        Member member = Member.of(
                userInfo.getEmail(),       // email
                "",                         // password
                now,                       // createdAt
                now                       // modifiedAt
        );
        member = memberRepository.saveAndFlush(member);
        AccountInfo accountInfo = AccountInfo.of(
                member,
                RoleType.USER,
                providerType,
                AccountStatus.ACTIVE,
                now,
                now,
                now
        );
        accountInfoRepository.save(accountInfo);

        return member;
    }

//    private OAuth2User processAppleUser(OAuth2UserRequest userRequest, OAuth2User user) {
//        try {
//            // ID 토큰 검증
//            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
//            if (!tokenProvider.validateAppleToken(idToken)) {
//                throw new OAuth2AuthenticationException("Invalid Apple ID token");
//            }
//
//            // Apple은 첫 로그인 시에만 사용자 정보를 제공하므로 이를 처리
//            Map<String, Object> attributes = user.getAttributes();
//            String email = (String) attributes.get("email");
//
//            // 이메일로 기존 회원 조회
//            Member savedMember = memberRepository.findByEmail(email).orElse(null);
//
//            if (savedMember != null) {
//                // 기존 회원의 경우 프로바이더 타입 확인
//                if (ProviderType.APPLE != savedMember.getProviderType()) {
//                    throw new OAuthProviderMissMatchException(
//                            "Looks like you're signed up with " + ProviderType.APPLE +
//                                    " account. Please use your " + savedMember.getProviderType() + " account to login."
//                    );
//                }
//
//                // 사용자 정보 업데이트 (첫 로그인 시에만 제공되는 정보)
//                Map<String, Object> userAttributes =
//                        (Map<String, Object>) userRequest.getAdditionalParameters().get("user");
//                if (userAttributes != null && userAttributes.containsKey("name")) {
//                    Map<String, String> name = (Map<String, String>) userAttributes.get("name");
//                    String fullName = name.getOrDefault("firstName", "") +
//                            " " +
//                            name.getOrDefault("lastName", "");
//                    if (!fullName.trim().isEmpty()) {
//                        savedMember.setUserNickname(fullName);
//                        memberRepository.save(savedMember);
//                    }
//                }
//            } else {
//                // 새로운 회원 생성
//                Map<String, Object> userAttributes =
//                        (Map<String, Object>) userRequest.getAdditionalParameters().get("user");
//
//                // 기본 사용자 정보 설정
//                Map<String, Object> finalAttributes = new HashMap<>(attributes);
//                if (userAttributes != null && userAttributes.containsKey("name")) {
//                    finalAttributes.put("name", userAttributes.get("name"));
//                }
//
//                OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
//                        ProviderType.APPLE,
//                        finalAttributes
//                );
//                savedMember = createMember(userInfo, ProviderType.APPLE);
//            }
//
//            return UserPrincipal.create(savedMember, attributes);
//
//        } catch (Exception ex) {
//            throw new OAuth2AuthenticationException(ex.getMessage());
//        }
//    }
}