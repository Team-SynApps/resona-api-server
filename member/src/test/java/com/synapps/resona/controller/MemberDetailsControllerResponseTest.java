package com.synapps.resona.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.resona.command.controller.MemberDetailsController;
import com.synapps.resona.command.dto.request.member_details.MemberDetailsRequest;
import com.synapps.resona.command.dto.response.MemberDetailsResponse;
import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.command.service.MemberDetailsService;
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
    controllers = MemberDetailsController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
@Disabled
class MemberDetailsControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MemberDetailsService memberDetailsService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    // 공통 Mock 설정
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("개인정보 등록 성공 시, 등록된 MemberDetailsDto를 반환한다")
  void registerPersonalInfo_success() throws Exception {
    // given
    MemberDetailsRequest requestDto = new MemberDetailsRequest(
        9, "010-1234-5678", MBTI.ISTJ, "Hello", "Seoul"
    );

    MemberDetailsResponse responseDto = MemberDetailsResponse.builder()
        .id(1L)
        .timezone(9)
        .phoneNumber("010-1234-5678")
        .mbti(MBTI.ISTJ)
        .aboutMe("Hello")
        .location("Seoul")
        .build();

    given(memberDetailsService.register(any(MemberDetailsRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/member-details")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
        .andExpect(jsonPath("$.data.mbti").value("ISTJ"))
        .andDo(print());
  }

  @Test
  @DisplayName("개인정보 조회 성공 시, MemberDetailsDto를 반환한다")
  void readPersonalInfo_success() throws Exception {
    // given
    MemberDetailsResponse responseDto = MemberDetailsResponse.builder()
        .id(1L)
        .timezone(9)
        .aboutMe("Existing User Info")
        .build();
    given(memberDetailsService.getMemberDetails()).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/member-details")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.aboutMe").value("Existing User Info"))
        .andDo(print());
  }

  @Test
  @DisplayName("개인정보 수정 성공 시, 수정된 MemberDetails 엔티티 정보를 반환한다")
  void editPersonalInfo_success() throws Exception {
    // given
    MemberDetailsRequest requestDto = new MemberDetailsRequest(
        -5, "010-9999-8888", MBTI.ENFP, "Updated Info", "Busan"
    );
    // 서비스는 DTO가 아닌 MemberDetails 엔티티를 반환하므로, 테스트용 엔티티를 생성
    MemberDetails responseEntity = MemberDetails.of(-5, "010-9999-8888", MBTI.ENFP, "Updated Info", "Busan");
    given(memberDetailsService.editMemberDetails(any(MemberDetailsRequest.class))).willReturn(responseEntity);

    // when
    ResultActions actions = mockMvc.perform(put("/member-details")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.timezone").value(-5))
        .andExpect(jsonPath("$.data.phoneNumber").value("010-9999-8888"))
        .andExpect(jsonPath("$.data.mbti").value("ENFP"))
        .andDo(print());
  }

  @Test
  @DisplayName("개인정보 삭제 성공 시, 삭제 처리된 MemberDetails 엔티티 정보를 반환한다")
  void deletePersonalInfo_success() throws Exception {
    // given
    MemberDetails responseEntity = MemberDetails.empty(); // 삭제 후 반환될 객체
    given(memberDetailsService.deleteMemberDetails()).willReturn(responseEntity);

    // when
    ResultActions actions = mockMvc.perform(delete("/member-details")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data.phoneNumber").value(""))
        .andDo(print());
  }
}