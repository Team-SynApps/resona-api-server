package com.synapps.resona.command.entity.member;

import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.account.AccountInfo;
import com.synapps.resona.command.entity.account.RoleType;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Setter
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser, CredentialsContainer,
    AuthenticatedUser {

  private final Long memberId;
  private final String email;
  private String password;
  private final ProviderType providerType;
  private final RoleType roleType;
  private final Collection<GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  public UserPrincipal(Long memberId, String email, String password, ProviderType providerType, RoleType roleType, Collection<GrantedAuthority> authorities) {
    this.memberId = memberId;
    this.email = email;
    this.password = password;
    this.providerType = providerType;
    this.roleType = roleType;
    this.authorities = authorities;
  }

  /**
   * JWT 기반 인증 시 사용될 UserPrincipal 생성 메소드.
   * 이 시점에서는 최초 로그인 방식(ProviderType)이 중요하지 않으므로 LOCAL을 기본값으로 사용합니다.
   * @param member Member 엔티티
   * @return UserPrincipal 객체
   */
  public static UserPrincipal create(Member member) {
    AccountInfo accountInfo = member.getAccountInfo();
    return new UserPrincipal(
        member.getId(),
        member.getEmail(),
        member.getPassword(),
        ProviderType.LOCAL, // JWT 인증 시에는 ProviderType이 중요하지 않으므로 기본값 설정
        accountInfo.getRoleType(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accountInfo.getRoleType().getCode()))
    );
  }

  /**
   * OAuth2 소셜 로그인 시 사용될 UserPrincipal 생성 메소드.
   * @param member Member 엔티티
   * @param providerType 소셜 로그인 제공자 타입
   * @param attributes OAuth2 제공자로부터 받은 속성 정보
   * @return UserPrincipal 객체
   */
  public static UserPrincipal create(Member member, ProviderType providerType, Map<String, Object> attributes) {
    UserPrincipal userPrincipal = create(member, providerType);
    userPrincipal.setAttributes(attributes);
    return userPrincipal;
  }

  /**
   * OAuth2 소셜 로그인 시 사용될 UserPrincipal 생성 메소드. (attributes가 없는 경우)
   * @param member Member 엔티티
   * @param providerType 소셜 로그인 제공자 타입
   * @return UserPrincipal 객체
   */
  public static UserPrincipal create(Member member, ProviderType providerType) {
    AccountInfo accountInfo = member.getAccountInfo();
    return new UserPrincipal(
        member.getId(),
        member.getEmail(),
        member.getPassword(),
        providerType,
        accountInfo.getRoleType(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accountInfo.getRoleType().getCode()))
    );
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getName() {
    return email;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Map<String, Object> getClaims() {
    return null;
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return null;
  }

  @Override
  public OidcIdToken getIdToken() {
    return null;
  }
}
