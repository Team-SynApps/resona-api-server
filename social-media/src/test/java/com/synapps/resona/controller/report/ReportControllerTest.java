//package com.synapps.resona.controller.report;
//
//import static org.mockito.ArgumentMatchers.any;
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
//import com.synapps.resona.report.controller.ReportController;
//import com.synapps.resona.report.dto.request.CommentReportRequest;
//import com.synapps.resona.report.dto.request.FeedReportRequest;
//import com.synapps.resona.report.dto.request.ReplyReportRequest;
//import com.synapps.resona.report.entity.ReportCategory;
//import com.synapps.resona.report.service.ReportService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//@WebMvcTest(
//    controllers = ReportController.class,
//    excludeAutoConfiguration = {
//        OAuth2ClientAutoConfiguration.class
//    }
//)
//@MockBean(JpaMetamodelMappingContext.class)
//@Disabled
//class ReportControllerTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @MockBean
//  private ReportService reportService;
//
//  @MockBean
//  private ServerInfoConfig serverInfo;
//
//  @BeforeEach
//  void setUp() {
//    // 공통 Mock 설정
//    given(serverInfo.getApiVersion()).willReturn("v1");
//    given(serverInfo.getServerName()).willReturn("test-server");
//  }
//
//  @Test
//  @WithMockUserPrincipal
//  @DisplayName("피드 신고 성공 시, 200 OK와 성공 메시지를 반환한다")
//  void reportFeed_success() throws Exception {
//    // given
//    long feedId = 10L;
//    FeedReportRequest requestDto = new FeedReportRequest(feedId, 2L, ReportCategory.IMPERSONATION);
//
//    doNothing().when(reportService).reportFeed(any(FeedReportRequest.class), any(MemberDto.class));
//
//    // when
//    ResultActions actions = mockMvc.perform(post("/feed/{feedId}/report", feedId)
//        .with(csrf())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(requestDto)));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.meta.message").value("Feed reported successfully."))
//        .andExpect(jsonPath("$.data").doesNotExist())
//        .andDo(print());
//  }
//
//  @Test
//  @WithMockUserPrincipal
//  @DisplayName("댓글 신고 성공 시, 200 OK와 성공 메시지를 반환한다")
//  void reportComment_success() throws Exception {
//    // given
//    long commentId = 20L;
//    CommentReportRequest requestDto = new CommentReportRequest(commentId, 3L, ReportCategory.IMPERSONATION);
//
//    doNothing().when(reportService).reportComment(any(CommentReportRequest.class), any(MemberDto.class));
//
//    // when
//    ResultActions actions = mockMvc.perform(post("/comment/{commentId}/report", commentId)
//        .with(csrf())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(requestDto)));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.meta.message").value("Comment reported successfully."))
//        .andExpect(jsonPath("$.data").doesNotExist())
//        .andDo(print());
//  }
//
//  @Test
//  @WithMockUserPrincipal
//  @DisplayName("대댓글 신고 성공 시, 200 OK와 성공 메시지를 반환한다")
//  void reportReply_success() throws Exception {
//    // given
//    long replyId = 30L;
//    ReplyReportRequest requestDto = new ReplyReportRequest(replyId, 4L, ReportCategory.IMPERSONATION);
//
//    doNothing().when(reportService).reportReply(any(ReplyReportRequest.class), any(MemberDto.class));
//
//    // when
//    ResultActions actions = mockMvc.perform(post("/reply/{replyId}/report", replyId)
//        .with(csrf())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(requestDto)));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.meta.message").value("Reply reported successfully."))
//        .andExpect(jsonPath("$.data").doesNotExist())
//        .andDo(print());
//  }
//}