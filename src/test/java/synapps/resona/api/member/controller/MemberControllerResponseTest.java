package synapps.resona.api.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.member.controller.MemberController;
import synapps.resona.api.member.dto.request.auth.LoginRequest;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.dto.response.MemberInfoDto;
import synapps.resona.api.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.member.dto.response.TokenResponse;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.service.AuthService;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.token.AuthToken;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(
    controllers = MemberController.class,
    excludeAutoConfiguration = {
//        SecurityAutoConfiguration.class, // Security 자동 설정 해제
        OAuth2ClientAutoConfiguration.class // OAuth2 클라이언트 자동 설정 해제
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MemberService memberService;

  @MockBean
  private AuthService authService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    // 모든 테스트에서 공통으로 사용되는 Mock 설정
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
  @DisplayName("회원가입 성공 시, 상세 회원 정보와 AuthToken을 포함한 응답을 반환한다")
  void join_success_with_detailed_dto() throws Exception {
    // given
    RegisterRequest registerRequest = new RegisterRequest(
        "test@example.com",
        "testTag123",
        "validPassword123!",
        CountryCode.KR, CountryCode.US, Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        9,
        LocalDate.of(1995, 5, 10).format(DateTimeFormatter.ISO_LOCAL_DATE),
        "tester",
        "http://example.com/profile.jpg"
    );

    MemberRegisterResponseDto registerResponse = MemberRegisterResponseDto.builder()
        .memberId(1L).email(registerRequest.getEmail()).nickname(registerRequest.getNickname()).tag(registerRequest.getTag())
        .build();

    AuthToken mockAccessToken = mock(AuthToken.class);
    AuthToken mockRefreshToken = mock(AuthToken.class);
    given(mockAccessToken.getToken()).willReturn("fake-access-token-string");
    given(mockRefreshToken.getToken()).willReturn("fake-refresh-token-string");
    TokenResponse tokenResponse = new TokenResponse(mockAccessToken, mockRefreshToken, true);

    given(memberService.signUp(any(RegisterRequest.class))).willReturn(registerResponse);
    given(authService.login(any(LoginRequest.class))).willReturn(tokenResponse);

    // when
    ResultActions actions = mockMvc.perform(
        post("/member/join")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest))
    );

    // then
    actions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.memberInfo.email").value(registerRequest.getEmail()))
        .andExpect(jsonPath("$.data.memberInfo.nickname").value(registerRequest.getNickname()))
        .andExpect(jsonPath("$.data.tokenInfo.accessToken.token").value("fake-access-token-string"))
        .andExpect(jsonPath("$.data.tokenInfo.refreshToken.token").value("fake-refresh-token-string"))
        .andExpect(jsonPath("$.data.tokenInfo.registered").value(true))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
  @DisplayName("사용자 정보 조회 성공 시, MemberInfoDto를 포함한 응답을 반환한다")
  void getUser_success() throws Exception {
    // when
    ResultActions actions = mockMvc.perform(get("/member/info")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.email").value("test@example.com"))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
  @DisplayName("사용자 상세 정보 조회 성공 시, MemberInfoDto를 포함한 응답을 반환한다")
  void getMemberDetailInfo_success() throws Exception {
    // given
    MemberInfoDto mockMemberDetailInfo = MemberInfoDto.builder()
        .nickname("detail-user")
        .aboutMe("I am a software engineer.")
        .mbti("ISTJ")
        .build();

    given(memberService.getMemberDetailInfo(anyString())).willReturn(mockMemberDetailInfo);

    // when
    ResultActions actions = mockMvc.perform(get("/member/detail")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.nickname").value("detail-user"))
        .andExpect(jsonPath("$.data.aboutMe").value("I am a software engineer."))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal
  @DisplayName("비밀번호 변경 성공 시, 성공 메시지를 포함한 응답을 반환한다")
  void changePassword_success() throws Exception {
    // given
    MemberPasswordChangeDto requestBody = new MemberPasswordChangeDto("test@example.com", "newPass");
    MemberDto successResponse = MemberDto.of(1L, "test@example.com");
    given(memberService.changePassword(any(), any(MemberPasswordChangeDto.class))).willReturn(successResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/member/password")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal
  @DisplayName("회원 탈퇴 성공 시, 성공 메시지를 포함한 응답을 반환한다")
  void deleteUser_success() throws Exception {
    // given
    Map<String, String> successResponse = Map.of("message", "User deleted successfully.");
    given(memberService.deleteUser()).willReturn(successResponse);

    // when
    ResultActions actions = mockMvc.perform(delete("/member")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data[0].message").value("User deleted successfully."))
        .andDo(print());
  }
}