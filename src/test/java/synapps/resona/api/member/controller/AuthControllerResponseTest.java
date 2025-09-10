package synapps.resona.api.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.member.controller.AuthController;
import synapps.resona.api.member.dto.request.auth.AppleLoginRequest;
import synapps.resona.api.member.dto.request.auth.LoginRequest;
import synapps.resona.api.member.dto.request.auth.RefreshRequest;
import synapps.resona.api.member.dto.response.ChatMemberDto;
import synapps.resona.api.member.dto.response.TokenResponse;
import synapps.resona.api.member.service.AuthService;
import synapps.resona.api.token.AuthToken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class AuthControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AuthService authService;

  @MockBean
  private ServerInfoConfig serverInfo;

  private TokenResponse mockTokenResponse;

  @BeforeEach
  void setUp() {
    // 공통 Mock 설정
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");

    // 공통으로 사용될 TokenResponse Mock 객체 생성
    AuthToken mockAccessToken = mock(AuthToken.class);
    AuthToken mockRefreshToken = mock(AuthToken.class);
    given(mockAccessToken.getToken()).willReturn("fake-access-token");
    given(mockRefreshToken.getToken()).willReturn("fake-refresh-token");
    mockTokenResponse = new TokenResponse(mockAccessToken, mockRefreshToken, true);
  }

  @Test
  @DisplayName("일반 로그인 성공 시, 토큰 정보를 포함한 응답을 반환한다")
  void authenticateUser_success() throws Exception {
    // given
    LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
    given(authService.login(any(LoginRequest.class))).willReturn(mockTokenResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/auth")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.accessToken.token").value("fake-access-token"))
        .andExpect(jsonPath("$.data.registered").value(true))
        .andDo(print());
  }

  @Test
  @DisplayName("애플 로그인 성공 시, 토큰 정보를 포함한 응답을 반환한다")
  void appleLogin_success() throws Exception {
    // given
    AppleLoginRequest appleRequest = new AppleLoginRequest("apple-identity-token");
    given(authService.appleLogin(any(), any(), any(AppleLoginRequest.class))).willReturn(mockTokenResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/auth/apple")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(appleRequest)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.accessToken.token").value("fake-access-token"))
        .andDo(print());
  }

  @Test
  @DisplayName("토큰 재발급 성공 시, 새로운 토큰 정보를 포함한 응답을 반환한다")
  void refreshToken_success() throws Exception {
    // given
    RefreshRequest refreshRequest = new RefreshRequest("existing-refresh-token");
    given(authService.refresh(any(), any(RefreshRequest.class))).willReturn(mockTokenResponse);

    // when
    // 참고: GET 요청에 @RequestBody를 사용하는 것은 표준적이지 않으나, 컨트롤러 코드에 맞춰 테스트를 작성합니다.
    ResultActions actions = mockMvc.perform(get("/auth/refresh-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(refreshRequest)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.refreshToken.token").value("fake-refresh-token"))
        .andDo(print());
  }

  @Test
  @DisplayName("회원 존재 여부 확인 시, ChatMemberDto를 포함한 응답을 반환한다")
  void memberExists_success() throws Exception {
    // given
    ChatMemberDto memberResponse = new ChatMemberDto("test@example.com", true);
    given(authService.isMember(any(), any())).willReturn(memberResponse);

    // when
    ResultActions actions = mockMvc.perform(get("/auth/member")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.memberEmail").value("test@example.com"))
        .andExpect(jsonPath("$.data.isMember").value(true))
        .andDo(print());
  }

  @Test
  @DisplayName("로그인 실패(AuthenticationException) 시, @ExceptionHandler가 동작하여 401 응답을 반환한다")
  void handleAuthenticationException() throws Exception {
    // given
    LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrong_password");
    // 서비스 계층에서 AuthenticationException (또는 그 자식 클래스) 예외를 던지도록 설정
    given(authService.login(any(LoginRequest.class)))
        .willThrow(new BadCredentialsException("Invalid credentials"));

    // when
    ResultActions actions = mockMvc.perform(post("/auth")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)));

    // then
    actions.andExpect(status().isUnauthorized()) // HTTP 401 상태 코드를 기대
        .andExpect(jsonPath("$.meta.status").value(401))
        .andExpect(jsonPath("$.meta.message").value("The account information does not match"))
        .andExpect(jsonPath("$.data").value("Invalid credentials"))
        .andDo(print());
  }
}