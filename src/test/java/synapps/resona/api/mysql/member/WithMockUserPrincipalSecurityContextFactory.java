package synapps.resona.api.mysql.member;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.UserPrincipal;
import java.util.Collections;
import java.util.List;

public class WithMockUserPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

  @Override
  public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + annotation.role()));

    UserPrincipal principal = new UserPrincipal(
        annotation.memberId(),
        annotation.email(),
        "password",
        ProviderType.LOCAL,
        RoleType.valueOf(annotation.role()),
        authorities
    );

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(principal, "password", authorities);

    context.setAuthentication(authenticationToken);
    return context;
  }
}