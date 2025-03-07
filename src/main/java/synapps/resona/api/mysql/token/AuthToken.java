package synapps.resona.api.mysql.token;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class AuthToken {
    private static final String AUTHORITIES_KEY = "role";
    private static final String PERMISSIONS_KEY = "permissions";
    private final Logger logger = LogManager.getLogger(AuthToken.class);
    @Getter
    private final String token;
    private final Key key;

    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    AuthToken(String id, String role, List<String> permissions, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, permissions, expiry);
    }

    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, String role, List<String> permissions, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role)
                .claim(PERMISSIONS_KEY, permissions)  // 권한
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }
}
