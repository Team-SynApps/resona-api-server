package synapps.resona.api.oauth.token;


import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import synapps.resona.api.oauth.exception.TokenValidFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.net.URL;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenProvider {
    private final Key key;
    private static final String AUTHORITIES_KEY = "role";
    private static final String APPLE_ISS = "https://appleid.apple.com";
    private static final String APPLE_AUD = "https://appleid.apple.com";

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;


    public AuthTokenProvider(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthToken createAuthToken(String id, Date expiry) {
        return new AuthToken(id, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        return new AuthToken(id, role, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public Authentication getAuthentication(AuthToken authToken) {

        if(authToken.validate()) {

            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            log.debug("claims subject := [{}]", claims.getSubject());
            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            throw new TokenValidFailedException();
        }
    }

    // apple 관련 추가 검증 함수
    public boolean validateAppleToken(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Apple token 검증
            if (!APPLE_ISS.equals(claimsSet.getIssuer()) ||
                    !appleClientId.equals(claimsSet.getAudience().get(0))) {
                return false;
            }

            // 만료 시간 검증
            Date currentTime = new Date();
            if (currentTime.after(claimsSet.getExpirationTime())) {
                return false;
            }

            // Apple의 public key로 서명 검증
            JWKSet jwkSet = JWKSet.load(new URL("https://appleid.apple.com/auth/keys"));
            JWK jwk = jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());

            if (jwk == null) {
                return false;
            }

            RSAKey rsaKey = (RSAKey) jwk;
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            return signedJWT.verify(verifier);

        } catch (Exception e) {
            log.error("Apple token validation failed", e);
            return false;
        }
    }

    public Claims getAppleTokenClaims(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Claims를 Map으로 변환
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", claimsSet.getSubject());
            claims.put("email", claimsSet.getClaim("email"));
            claims.put("email_verified", claimsSet.getClaim("email_verified"));

            // JWT Claims로 변환
            return Jwts.claims(claims);
        } catch (Exception e) {
            log.error("Failed to parse Apple token claims", e);
            throw new TokenValidFailedException("Failed to parse Apple token claims");
        }
    }

}
