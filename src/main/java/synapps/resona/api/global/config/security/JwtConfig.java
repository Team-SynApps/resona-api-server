package synapps.resona.api.global.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import synapps.resona.api.mysql.token.AuthTokenProvider;

@Configuration
public class JwtConfig {

  @Value("${jwt.secret:default}")
  private String secret;

  @Bean
  public AuthTokenProvider jwtProvider() {
    System.out.println(secret);
    return new AuthTokenProvider(secret);
  }
}