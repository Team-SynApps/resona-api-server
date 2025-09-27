//package com.synapps.resona.controller.mention;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.synapps.resona.config.server.ServerInfoConfig;
//import com.synapps.resona.mention.controller.MentionController;
//import com.synapps.resona.mention.dto.MentionResponse;
//import com.synapps.resona.mention.service.MentionService;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//
//@WebMvcTest(
//    controllers = MentionController.class,
//    excludeAutoConfiguration = {
//        SecurityAutoConfiguration.class,
//        OAuth2ClientAutoConfiguration.class
//    }
//)
//@MockBean(JpaMetamodelMappingContext.class)
//@Disabled
//class MentionControllerResponseTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @MockBean
//  private MentionService mentionService;
//
//  @MockBean
//  private ServerInfoConfig serverInfo;
//
//  private MentionResponse mockMentionResponse;
//
//  @BeforeEach
//  void setUp() {
//    given(serverInfo.getApiVersion()).willReturn("v1");
//    given(serverInfo.getServerName()).willReturn("test-server");
//
//    // 테스트에서 공통으로 사용할 MentionResponseDto 생성
//    mockMentionResponse = MentionResponse.of(1L, 101L, 201L, LocalDateTime.now());
//  }
//
//  @Test
//  @DisplayName("맨션 등록 성공 시, 201 Created와 MentionResponseDto를 반환한다")
//  void registerMention_success() throws Exception {
//    // given
//    Long commentId = 201L;
//    given(mentionService.register(anyLong())).willReturn(mockMentionResponse);
//
//    // when
//    ResultActions actions = mockMvc.perform(post("/mention/{commentId}", commentId)
//        .contentType(MediaType.APPLICATION_JSON));
//
//    // then
//    actions.andExpect(status().isCreated()) // 201 Created
//        .andExpect(jsonPath("$.meta.status").value(201))
//        .andExpect(jsonPath("$.data.mentionId").value(1L))
//        .andExpect(jsonPath("$.data.memberId").value(101L))
//        .andExpect(jsonPath("$.data.commentId").value(201L))
//        .andDo(print());
//  }
//
//  @Test
//  @DisplayName("맨션 단건 조회 성공 시, MentionResponseDto를 반환한다")
//  void readMention_success() throws Exception {
//    // given
//    Long mentionId = 1L;
//    given(mentionService.read(anyLong())).willReturn(mockMentionResponse);
//
//    // when
//    ResultActions actions = mockMvc.perform(get("/mention/{mentionId}", mentionId)
//        .contentType(MediaType.APPLICATION_JSON));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data.mentionId").value(1L))
//        .andExpect(jsonPath("$.data.memberId").value(101L))
//        .andExpect(jsonPath("$.data.commentId").value(201L))
//        .andDo(print());
//  }
//
//  @Test
//  @DisplayName("맨션 삭제 성공 시, 200 OK와 성공 메세지를 반환한다")
//  void deleteMention_success() throws Exception {
//    // given
//    Long mentionId = 1L;
//    // mentionService.delete는 void를 반환하므로 doNothing()으로 설정
//    doNothing().when(mentionService).delete(anyLong());
//
//    // when
//    ResultActions actions = mockMvc.perform(delete("/mention/{mentionId}", mentionId)
//        .contentType(MediaType.APPLICATION_JSON));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data").doesNotExist()) // data 필드가 없음을 확인
//        .andDo(print());
//
//    verify(mentionService).delete(mentionId);
//  }
//}
