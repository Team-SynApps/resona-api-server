package synapps.resona.api.mysql.member.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.mysql.member.dto.request.profile.DuplicateTagRequest;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.dto.response.ProfileDto;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.service.ProfileService;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Gender;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ProfileController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class ProfileControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ProfileService profileService;

  @MockBean
  private ServerInfoConfig serverInfo;


  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("프로필 등록 성공 시, 등록된 ProfileDto를 반환한다")
  void registerProfile_success() throws Exception {
    // given
    ProfileRequest requestDto = new ProfileRequest(
        "test-nick",
        CountryCode.KR, CountryCode.US,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        "profile.jpg",
        "bg.jpg",
        "2000-01-01",
        Gender.MAN,
        "Hello");
    ProfileDto responseDto = ProfileDto.builder().id(1L).nickname("test-nick").tag("test-tag").build();

    given(profileService.register(any(ProfileRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0].id").value(1L))
        .andExpect(jsonPath("$.data[0].nickname").value("test-nick"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 조회 성공 시, ProfileDto를 반환한다")
  void readProfile_success() throws Exception {
    // given
    ProfileDto responseDto = ProfileDto.builder().id(1L).nickname("test-user").tag("test-tag").build();
    given(profileService.readProfile()).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/profile")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0].id").value(1L))
        .andExpect(jsonPath("$.data[0].nickname").value("test-user"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 수정 성공 시, 수정된 ProfileDto를 반환한다")
  void editProfile_success() throws Exception {
    // given
    ProfileRequest requestDto = new ProfileRequest(
        "updated-nick",
        CountryCode.KR, CountryCode.US,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        "profile.jpg",
        "bg.jpg",
        "2000-01-01",
        Gender.MAN,
        "Updated Hello");
    ProfileDto responseDto = ProfileDto.builder().id(1L).nickname("updated-nick").tag("test-tag").build();
    given(profileService.editProfile(any(ProfileRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0].id").value(1L))
        .andExpect(jsonPath("$.data[0].nickname").value("updated-nick"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 삭제 성공 시, 삭제 처리된 Profile 엔티티를 반환한다")
  void deleteProfile_success() throws Exception {
    // given
    // 서비스는 Profile 엔티티를 반환하므로 Mock Entity를 생성
    Profile mockProfile = mock(Profile.class);
    given(mockProfile.getId()).willReturn(1L);
    given(mockProfile.getTag()).willReturn("deleted-tag");
    given(profileService.deleteProfile()).willReturn(mockProfile);

    // when
    ResultActions actions = mockMvc.perform(delete("/profile")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0].id").value(1L))
        .andExpect(jsonPath("$.data[0].tag").value("deleted-tag"))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 중복 확인 시, 중복 여부(boolean)를 반환한다")
  void checkDuplicateTag_success() throws Exception {
    // given
    DuplicateTagRequest requestDto = new DuplicateTagRequest("existing-tag");
    given(profileService.checkDuplicateTag(anyString())).willReturn(true); // 중복되는 경우

    // when
    ResultActions actions = mockMvc.perform(post("/profile/duplicate-tag")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0]").value(true))
        .andDo(print());
  }
}