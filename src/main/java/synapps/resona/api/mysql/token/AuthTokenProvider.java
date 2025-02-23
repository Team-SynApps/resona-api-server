package synapps.resona.api.mysql.token;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import synapps.resona.api.oauth.exception.TokenValidFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.*;



public class AuthTokenProvider {
    private final Key key;
    private static final String AUTHORITIES_KEY = "role";
    private static final String PERMISSIONS_KEY = "permissions";
    private static final Logger logger = LogManager.getLogger(AuthTokenProvider.class);


    public AuthTokenProvider(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthToken createAuthToken(String id, Date expiry) {
        return new AuthToken(id, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        return new AuthToken(id, role, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, List<String> permissions, Date expiry) {
        return new AuthToken(id, role, permissions, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public Authentication getAuthentication(AuthToken authToken) {
        if(authToken.validate()) {
            Claims claims = authToken.getTokenClaims();

            List<GrantedAuthority> authorities = new ArrayList<>();

            // 역할 처리
            String role = claims.get(AUTHORITIES_KEY).toString();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            // 권한 처리
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) claims.get(PERMISSIONS_KEY);
            if (permissions != null) {
                authorities.addAll(
                        permissions.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                );
            }

            logger.debug("Authorities after combining roles and permissions: [{}]", authorities);

            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            throw new TokenValidFailedException();
        }
    }

}
