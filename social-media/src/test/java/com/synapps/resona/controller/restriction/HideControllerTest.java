//package com.synapps.resona.controller.restriction;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.synapps.resona.config.WithMockUserPrincipal;
//import com.synapps.resona.config.server.ServerInfoConfig;
//import com.synapps.resona.member.dto.MemberDto;
//import com.synapps.resona.command.controller.restriction.HideController;
//import com.synapps.resona.query.service.HideService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//@WebMvcTest(
//    controllers = HideController.class,
//    excludeAutoConfiguration = {
//        OAuth2ClientAutoConfiguration.class
//    }
//)
//@MockBean(JpaMetamodelMappingContext.class)
//@Disabled
//class HideControllerTest {
////
////  @Autowired
////  private MockMvc mockMvc;
////
////  @Autowired
////  private ObjectMapper objectMapper;
////
////  @MockBean
////  private HideService hideService;
////
////  @MockBean
////  private ServerInfoConfig serverInfo;
////
////  @BeforeEach
////  void setUp() {
////    given(serverInfo.getApiVersion()).willReturn("v1");
////    given(serverInfo.getServerName()).willReturn("test-server");
////  }
////
////  @Test
////  @WithMockUserPrincipal
////  @DisplayName("피드 숨김 성공 시, 200 OK와 성공 메시지를 반환한다")
////  void hideFeed_success() throws Exception {
////    // given
////    long feedId = 10L;
////    doNothing().when(hideService).hideFeed(anyLong(), any(MemberDto.class));
////
////    // when
////    ResultActions actions = mockMvc.perform(post("/feed/{feedId}/hide", feedId)
////        .with(csrf()));
////
////    // then
////    actions.andExpect(status().isOk())
////        .andExpect(jsonPath("$.meta.status").value(200))
////        .andExpect(jsonPath("$.meta.message").value("Feed hidden successfully."))
////        .andExpect(jsonPath("$.data").doesNotExist())
////        .andDo(print());
////  }
////
////  @Test
////  @WithMockUserPrincipal
////  @DisplayName("댓글 숨김 성공 시, 200 OK와 성공 메시지를 반환한다")
////  void hideComment_success() throws Exception {
////    // given
////    long commentId = 20L;
////    doNothing().when(hideService).hideComment(anyLong(), any(MemberDto.class));
////
////    // when
////    ResultActions actions = mockMvc.perform(post("/comment/{commentId}/hide", commentId)
////        .with(csrf()));
////
////    // then
////    actions.andExpect(status().isOk())
////        .andExpect(jsonPath("$.meta.status").value(200))
////        .andExpect(jsonPath("$.meta.message").value("Comment hidden successfully."))
////        .andExpect(jsonPath("$.data").doesNotExist())
////        .andDo(print());
////  }
////
////  @Test
////  @WithMockUserPrincipal
////  @DisplayName("대댓글 숨김 성공 시, 200 OK와 성공 메시지를 반환한다")
////  void hideReply_success() throws Exception {
////    // given
////    long replyId = 30L;
////    doNothing().when(hideService).hideReply(anyLong(), any(MemberDto.class));
////
////    // when
////    ResultActions actions = mockMvc.perform(post("/reply/{replyId}/hide", replyId)
////        .with(csrf()));
////
////    // then
////    actions.andExpect(status().isOk())
////        .andExpect(jsonPath("$.meta.status").value(200))
////        .andExpect(jsonPath("$.meta.message").value("Reply hidden successfully."))
////        .andExpect(jsonPath("$.data").doesNotExist())
////        .andDo(print());
////  }
//}