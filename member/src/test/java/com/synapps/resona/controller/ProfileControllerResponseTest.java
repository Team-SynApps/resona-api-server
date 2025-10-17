package com.synapps.resona.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.resona.command.controller.ProfileController;
import com.synapps.resona.command.dto.MemberDto; // Import MemberDto
import com.synapps.resona.command.dto.request.profile.DuplicateTagRequest;
import com.synapps.resona.command.dto.request.profile.ProfileRequest;
import com.synapps.resona.command.dto.response.ProfileResponse;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.command.service.ProfileService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@WebMvcTest(
    controllers = ProfileController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
@Disabled
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
    // Simulate an authenticated user
  void registerProfile_success() throws Exception {
    // given
    ProfileRequest requestDto = new ProfileRequest(
        "test-nick",
        CountryCode.KR, CountryCode.US,
        new HashSet<>(Set.of("KO")), new HashSet<>(Set.of("EN")),
        "profile.jpg",
        "bg.jpg",
        "2000-01-01",
        Gender.MAN,
        "Hello");
    ProfileResponse responseDto = ProfileResponse.builder().id(1L).nickname("test-nick").tag("test-tag").build();

    // The service method now expects a MemberDto, so we use any(MemberDto.class)
    given(profileService.register(any(ProfileRequest.class), any(MemberDto.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("test-nick"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 조회 성공 시, ProfileDto를 반환한다")
    // Simulate an authenticated user
  void readProfile_success() throws Exception {
    // given
    ProfileResponse responseDto = ProfileResponse.builder().id(1L).nickname("test-user").tag("test-tag").build();
    // The service method now expects a MemberDto
    given(profileService.readProfile(any(MemberDto.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/profile")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("test-user"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 수정 성공 시, 수정된 ProfileDto를 반환한다")
    // Simulate an authenticated user
  void editProfile_success() throws Exception {
    // given
    ProfileRequest requestDto = new ProfileRequest(
        "updated-nick",
        CountryCode.KR, CountryCode.US,
        new HashSet<>(Set.of("KO")), new HashSet<>(Set.of("EN")),
        "profile.jpg",
        "bg.jpg",
        "2000-01-01",
        Gender.MAN,
        "Updated Hello");
    ProfileResponse responseDto = ProfileResponse.builder().id(1L).nickname("updated-nick").tag("test-tag").build();
    // The service method now expects a MemberDto
    given(profileService.editProfile(any(ProfileRequest.class), any(MemberDto.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("updated-nick"))
        .andDo(print());
  }

  @Test
  @DisplayName("프로필 삭제 성공 시, 성공 응답을 반환한다")
    // Simulate an authenticated user
  void deleteProfile_success() throws Exception {
    // given
    // The service method `deleteProfile` returns a Profile entity.
    // We mock this behavior. The controller should handle this and return a proper response.
    Profile mockProfile = mock(Profile.class);
    given(mockProfile.getId()).willReturn(1L);

    // The service method now expects a MemberDto
    given(profileService.deleteProfile(any(MemberDto.class))).willReturn(mockProfile);

    // when
    ResultActions actions = mockMvc.perform(delete("/profile")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    // Assuming the controller returns a successful response wrapper upon deletion.
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 중복 확인 시, 중복 여부(boolean)를 반환한다")
  void checkDuplicateTag_success() throws Exception {
    // given
    DuplicateTagRequest requestDto = new DuplicateTagRequest("existing-tag");
    // This service method does not require authentication, so it remains unchanged.
    given(profileService.checkDuplicateTag(anyString())).willReturn(true); // Case: tag is a duplicate

    // when
    ResultActions actions = mockMvc.perform(post("/profile/duplicate-tag")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").value(true))
        .andDo(print());
  }
}