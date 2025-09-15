package synapps.resona.api.socialMedia.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.fixture.ReplyFixture;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.comment.controller.ReplyController;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyRequest;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyUpdateRequest;
import synapps.resona.api.socialMedia.comment.dto.response.CommentDeleteResponse;
import synapps.resona.api.socialMedia.comment.service.ReplyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ReplyController.class,
    excludeAutoConfiguration = {
//        SecurityAutoConfiguration.class,
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
  @WithMockUserPrincipal(memberId = 1L)
  void registerReply_success() throws Exception {
    // given
    ReplyRequest requestDto = ReplyFixture.createReplyRequest(101L, "This is a reply.");
    ReplyDto responseDto = ReplyFixture.createReplyDto(101L, 201L, "This is a reply.");
    given(replyService.register(any(ReplyRequest.class), any(MemberDto.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/replies")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.commentId").value(101L))
        .andExpect(jsonPath("$.data.replyId").value(201L))
        .andExpect(jsonPath("$.data.author.memberId").value(1L))
        .andExpect(jsonPath("$.data.author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data.author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data.content").value("This is a reply."))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("답글 단건 조회 성공 시, ReplyReadResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getReply_success() throws Exception {
    // given
    Long memberId = 1L;
    Long commentId = 101L;
    List<ReplyDto> responseDto = ReplyFixture.createReplyDtoList(commentId);
    given(replyService.readAll(memberId, commentId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/replies/{commentId}", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data[0].commentId").value(101L))
        .andExpect(jsonPath("$.data[0].replyId").value(201L))
        .andExpect(jsonPath("$.data[0].author.memberId").value(1L))
        .andExpect(jsonPath("$.data[0].author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data[0].author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data[0].content").value("This is a reply."))
        .andExpect(jsonPath("$.data[0].createdAt").exists())
        .andExpect(jsonPath("$.data[1].commentId").value(101L))
        .andExpect(jsonPath("$.data[1].replyId").value(202L))
        .andExpect(jsonPath("$.data[1].author.memberId").value(1L))
        .andExpect(jsonPath("$.data[1].author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data[1].author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data[1].content").value("This is a reply 2."))
        .andExpect(jsonPath("$.data[1].createdAt").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("답글 수정 성공 시, 수정된 ReplyReadResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void updateReply_success() throws Exception {
    // given
    Long replyId = 201L;
    ReplyUpdateRequest requestDto = ReplyFixture.createReplyUpdateRequest(replyId, "Updated reply content.");
    ReplyDto responseDto = ReplyFixture.createReplyDto(101L, replyId, "Updated reply content.");
    given(replyService.update(any(ReplyUpdateRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/replies/{replyId}", replyId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentId").value(101L))
        .andExpect(jsonPath("$.data.replyId").value(201L))
        .andExpect(jsonPath("$.data.author.memberId").value(1L))
        .andExpect(jsonPath("$.data.author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data.author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data.content").value("Updated reply content."))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("답글 삭제 성공 시, 갱신된 댓글 수를 포함한 응답을 반환한다")
  @WithMockUserPrincipal
  void deleteReply_success() throws Exception {
    // given
    Long replyId = 201L;
    long expectedCommentCount = 22;
    CommentDeleteResponse responseDto = CommentDeleteResponse.of(expectedCommentCount);
    given(replyService.delete(replyId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(delete("/replies/{replyId}", replyId)
        .with(csrf()));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentCount").value(expectedCommentCount))
        .andDo(print());
  }
}

