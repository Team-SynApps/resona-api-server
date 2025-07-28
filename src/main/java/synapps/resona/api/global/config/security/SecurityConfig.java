package synapps.resona.api.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.filter.TokenAuthenticationFilter;
import synapps.resona.api.global.handler.CustomAccessDeniedHandler;
import synapps.resona.api.global.handler.CustomAuthenticationEntryPoint;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.global.properties.CorsProperties;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.repository.member.MemberRefreshTokenRepository;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.handler.OAuth2AuthenticationFailureHandler;
import synapps.resona.api.oauth.handler.OAuth2AuthenticationSuccessHandler;
import synapps.resona.api.oauth.resolver.CustomOAuth2AuthorizationRequestResolver;
import synapps.resona.api.oauth.respository.CustomOAuth2AuthorizationRequestRepository;
import synapps.resona.api.oauth.service.CustomOAuth2UserService;
import synapps.resona.api.oauth.service.CustomUserDetailsService;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  private final CorsProperties corsProperties;
  private final AppProperties appProperties;
  private final AuthTokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final ServerInfoConfig serverInfo;
  private final CustomUserDetailsService userDetailsService;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final MemberRepository memberRepository;
  private final CustomOAuth2UserService oAuth2UserService;
  private final MemberService memberService;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final Environment environment;

  private static final String[] PERMIT_URL_ARRAY = {
      /* swagger v3 */
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-resources/**",
      /* basic endpoints */
      "/auth",
      "/auth/refresh-token",
      "/actuator/**",
      "/email",
      "/email/verification",
      "/metrics",
      "/email/temp-token",
      "/auth/apple"
  };

  private static final String[] GUEST_PERMIT_URL_ARRAY = {
      "/member/password",
      "/member/join"
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

    if (!Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
      http.requiresChannel(channel ->
          channel.requestMatchers("/oauth2/**").requiresSecure()
      );
    }
//
//    http.requiresChannel(channel ->
//        channel.requestMatchers("/oauth2/**").requiresSecure()
//    );

    http.authorizeHttpRequests((authorizeHttp) -> authorizeHttp
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .requestMatchers(GUEST_PERMIT_URL_ARRAY)
        .hasAnyRole(RoleType.GUEST.getCode(), RoleType.USER.getCode(), RoleType.ADMIN.getCode())
        .requestMatchers("/api/v1/actuator/**").permitAll()
        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
        .requestMatchers("/api/v1/**").hasAnyRole(RoleType.ADMIN.getCode())
        .anyRequest().hasAnyRole(RoleType.USER.getCode(), RoleType.ADMIN.getCode()));

    http.exceptionHandling(exceptionHandling ->
        exceptionHandling
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper, serverInfo))
            .accessDeniedHandler(new CustomAccessDeniedHandler(serverInfo))
    );

    http.oauth2Login((oauth2LoginConfig) -> {
          oauth2LoginConfig
              .authorizationEndpoint((endpoint) -> endpoint
                  .baseUri("/oauth2/authorization")
                  .authorizationRequestRepository(oAuth2AuthorizationRequestRepository())
                  .authorizationRequestResolver(customOAuth2AuthorizationRequestResolver())
              )
              .redirectionEndpoint((endpoint) ->
                  endpoint.baseUri("/*/oauth2/code/*")
              )
              .userInfoEndpoint((endpoint) ->
                  endpoint.userService(oAuth2UserService))
              .successHandler(oAuth2AuthenticationSuccessHandler())
              .failureHandler(new OAuth2AuthenticationFailureHandler(objectMapper, oAuth2AuthorizationRequestRepository(), serverInfo));
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
        oAuth2AuthorizationRequestRepository(),
        memberService
    );
  }

  /*
   * Oauth 인증 실패 핸들러
   * */
  @Bean
  public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler(ObjectMapper objectMapper) {
    return new OAuth2AuthenticationFailureHandler(
        objectMapper,
        oAuth2AuthorizationRequestRepository(),
        serverInfo
    );
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
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }


  /*
   * 토큰 필터 설정
   * */
  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(tokenProvider, objectMapper, serverInfo, memberRepository);
  }

  /*
   * 쿠키 기반 인가 Repository
   * 인가 응답을 연계 하고 검증할 때 사용.
   * */
  @Bean
  public CustomOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository() {
    return new CustomOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver() {
    DefaultOAuth2AuthorizationRequestResolver defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/oauth2/authorization"
        );
    return new CustomOAuth2AuthorizationRequestResolver(defaultResolver);
  }
//
//    @Bean
//    public MemberSecurity memberSecurity(AuthTokenProvider authTokenProvider) {
//        return new MemberSecurity(authTokenProvider);
//    }
}