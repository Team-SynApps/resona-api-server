package synapps.resona.api.oauth.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.Member;

@Getter
@Setter
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser, CredentialsContainer {

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

  public static UserPrincipal create(Member member, AccountInfo accountInfo) {
    return new UserPrincipal(
        member.getId(),
        member.getEmail(),
        member.getPassword(),
        accountInfo.getProviderType(),
        accountInfo.getRoleType(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accountInfo.getRoleType().getCode()))
    );
  }

  public static UserPrincipal create(Member member, AccountInfo accountInfo,
      Map<String, Object> attributes) {
    UserPrincipal userPrincipal = create(member, accountInfo);
    userPrincipal.setAttributes(attributes);

    return userPrincipal;
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

  /**
   * 인증 후 Spring Security에 의해 호출되어 민감한 정보를 제거
   */
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