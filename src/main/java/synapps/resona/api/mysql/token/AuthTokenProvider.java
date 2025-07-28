package synapps.resona.api.mysql.token;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import synapps.resona.api.oauth.exception.TokenValidFailedException;


public class AuthTokenProvider {

  private static final String AUTHORITIES_KEY = "role";
  private static final String PERMISSIONS_KEY = "permissions";
  private static final Logger logger = LogManager.getLogger(AuthTokenProvider.class);
  private final Key key;


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

}
