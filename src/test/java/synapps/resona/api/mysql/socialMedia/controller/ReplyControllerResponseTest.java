package synapps.resona.api.mysql.socialMedia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.socialMedia.controller.comment.ReplyController;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyResponse;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;
import synapps.resona.api.mysql.socialMedia.service.comment.ReplyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ReplyController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class ReplyControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ReplyService replyService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("답글 등록 성공 시, 등록된 ReplyPostResponse를 반환한다")
  void registerReply_success() throws Exception {
    // given
    ReplyRequest requestDto = new ReplyRequest(101L, "This is a reply.");
    ReplyResponse responseDto = ReplyResponse.of(101L, 201L, "This is a reply.", LocalDateTime.now());
    given(replyService.register(any(ReplyRequest.class), any(MemberDto.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/replies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.replyId").value("201"))
        .andExpect(jsonPath("$.data.content").value("This is a reply."))
        .andDo(print());
  }

  @Test
  @DisplayName("답글 단건 조회 성공 시, ReplyReadResponse를 반환한다")
  void getReply_success() throws Exception {
    // given
    Long replyId = 201L;
    ReplyResponse responseDto = ReplyResponse.of(101L, 201L, "This is a reply.", LocalDateTime.now());
    given(replyService.read(replyId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/replies/{replyId}", replyId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.replyId").value("201"))
        .andDo(print());
  }

  @Test
  @DisplayName("답글 수정 성공 시, 수정된 ReplyReadResponse를 반환한다")
  void updateReply_success() throws Exception {
    // given
    Long replyId = 201L;
    ReplyUpdateRequest requestDto = new ReplyUpdateRequest(replyId, "Updated reply content.");
    ReplyResponse responseDto = ReplyResponse.of(101L, 201L, "Updated reply content.", LocalDateTime.now());
    given(replyService.update(any(ReplyUpdateRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/replies/{replyId}", replyId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.replyId").value("201"))
        .andExpect(jsonPath("$.data.content").value("Updated reply content."))
        .andDo(print());
  }

  @Test
  @DisplayName("답글 삭제 성공 시, 삭제 처리된 Reply 엔티티를 반환한다")
  void deleteReply_success() throws Exception {
    // given
    Long replyId = 201L;
    Reply mockReply = mock(Reply.class);
    given(mockReply.getId()).willReturn(replyId);
    given(mockReply.getContent()).willReturn("This reply was deleted.");
    given(replyService.delete(anyLong())).willReturn(mockReply);

    // when
    ResultActions actions = mockMvc.perform(delete("/replies/{replyId}", replyId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data.id").value(replyId))
//        .andExpect(jsonPath("$.data.content").value("This reply was deleted."))
        .andDo(print());
  }
}