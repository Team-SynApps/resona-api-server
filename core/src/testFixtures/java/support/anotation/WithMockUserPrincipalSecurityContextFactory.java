package support.anotation;

import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.account.RoleType;
import com.synapps.resona.command.entity.member.UserPrincipal;
import java.util.Collections;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserPrincipalSecurityContextFactory implements
    WithSecurityContextFactory<WithMockUserPrincipal> {

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