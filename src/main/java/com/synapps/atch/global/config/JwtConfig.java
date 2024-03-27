package com.synapps.atch.global.config;

import com.synapps.atch.oauth.token.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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