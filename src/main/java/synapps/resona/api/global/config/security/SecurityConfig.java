package synapps.resona.api.global.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.global.properties.CorsProperties;
import synapps.resona.api.mysql.member.repository.MemberRefreshTokenRepository;
import synapps.resona.api.oauth.exception.RestAuthenticationEntryPoint;
import synapps.resona.api.oauth.filter.TokenAuthenticationFilter;
import synapps.resona.api.oauth.handler.OAuth2AuthenticationFailureHandler;
import synapps.resona.api.oauth.handler.OAuth2AuthenticationSuccessHandler;
import synapps.resona.api.oauth.handler.TokenAccessDeniedHandler;
import synapps.resona.api.oauth.respository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import synapps.resona.api.oauth.service.CustomOAuth2UserService;
import synapps.resona.api.oauth.service.CustomUserDetailsService;
import synapps.resona.api.oauth.token.AuthTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import synapps.resona.api.oauth.entity.RoleType;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/auth",
            "/auth/refresh-token",
            "/member/join",
            "/actuator/**",
            "/email",
            // for testing
            "/storage/**"
    };

    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults());
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);

        http.userDetailsService(userDetailsService);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.requiresChannel(channel ->
                channel.requestMatchers("/oauth2/**").requiresSecure()
        );


        http.authorizeHttpRequests((authorizeHttp)-> authorizeHttp
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                .requestMatchers("/api/v1/**").hasAnyAuthority(RoleType.USER.getCode()).anyRequest().authenticated()
        );


        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(((request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: " + accessDeniedException.getMessage());
        })));


        http.oauth2Login((oauth2LoginConfig)-> {oauth2LoginConfig
                .authorizationEndpoint((endpoint)-> endpoint
                        .baseUri("/oauth2/authorization")
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                .redirectionEndpoint((endpoint) ->
                        endpoint.baseUri("/*/oauth2/code/*")
                )
                .userInfoEndpoint((endpoint)->
                        endpoint.userService(oAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler());
            }
        );

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
                memberRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }


    /*
     * Cors 설정
     * */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }

    /*
     * auth 매니저 설정
     * */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }


    /*
     * 토큰 필터 설정
     * */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}