package synapps.resona.api.socialMedia.comment.controller;

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
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.fixture.CommentFixture;
import synapps.resona.api.fixture.ReplyFixture;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.socialMedia.comment.controller.CommentController;
import synapps.resona.api.socialMedia.comment.dto.CommentDto;
import synapps.resona.api.socialMedia.comment.dto.request.CommentRequest;
import synapps.resona.api.socialMedia.comment.dto.request.CommentUpdateRequest;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.response.CommentDeleteResponse;
import synapps.resona.api.socialMedia.comment.service.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = CommentController.class,
    excludeAutoConfiguration = {
//        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommentService commentService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("댓글 등록 성공 시, 등록된 CommentDto를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void registerComment_success() throws Exception {
    // given
    CommentRequest requestDto = CommentFixture.createCommentRequest(1L, "New Comment");
    CommentDto responseDto = CommentFixture.createCommentDto(101L, "New Comment");
    given(commentService.register(any(CommentRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/comments")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.commentId").value(101L))
        .andExpect(jsonPath("$.data.author.memberId").value(1L))
        .andExpect(jsonPath("$.data.author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data.author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data.content").value("New Comment"))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andExpect(jsonPath("$.data.modifiedAt").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 단건 조회 성공 시, CommentDto를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getComment_success() throws Exception {
    // given
    Long commentId = 101L;
    CommentDto responseDto = CommentFixture.createCommentDto(commentId, "A single comment");
    given(commentService.getComment(1L, commentId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/{commentId}", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentId").value(commentId))
        .andExpect(jsonPath("$.data.author.memberId").value(1L))
        .andExpect(jsonPath("$.data.author.nickname").value("test_user"))
        .andExpect(jsonPath("$.data.author.profileImageUrl").value("test_url"))
        .andExpect(jsonPath("$.data.content").value("A single comment"))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andExpect(jsonPath("$.data.modifiedAt").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("피드 전체 댓글 조회 성공 시, CommentDto 리스트를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getComments_success() throws Exception {
    // given
    Long feedId = 1L;
    List<CommentDto> responseList = CommentFixture.createCommentDtoList();
    given(commentService.getCommentsByFeedId(1L, feedId)).willReturn(responseList);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/all/{feedId}", feedId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].commentId").value(101L))
        .andExpect(jsonPath("$.data[0].content").value("First comment"))
        .andExpect(jsonPath("$.data[1].commentId").value(102L))
        .andExpect(jsonPath("$.data[1].content").value("Second comment"))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글의 답글 조회 성공 시, ReplyDto 리스트를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getReplies_success() throws Exception {
    // given
    Long commentId = 101L;
    List<ReplyDto> responseList = ReplyFixture.createReplyDtoList(commentId);
    given(commentService.getReplies(1L, commentId)).willReturn(responseList);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/{commentId}/replies", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].replyId").value(201L))
        .andExpect(jsonPath("$.data[0].content").value("This is a reply."))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 수정 성공 시, 수정된 CommentDto를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void editComment_success() throws Exception {
    // given
    Long commentId = 101L;
    CommentUpdateRequest requestDto = CommentFixture.createCommentUpdateRequest(commentId, "Updated content");
    CommentDto responseDto = CommentFixture.createCommentDto(commentId, "Updated content");
    given(commentService.edit(any(CommentUpdateRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/comments/{commentId}", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentId").value(commentId))
        .andExpect(jsonPath("$.data.content").value("Updated content"))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 삭제 성공 시, 갱신된 댓글 수를 포함한 응답을 반환한다")
  @WithMockUserPrincipal
  void deleteComment_success() throws Exception {
    // given
    Long commentId = 101L;
    long expectedCommentCount = 15;
    CommentDeleteResponse responseDto = CommentDeleteResponse.of(expectedCommentCount);
    given(commentService.deleteComment(commentId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(delete("/comments/{commentId}", commentId)
        .with(csrf()));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentCount").value(expectedCommentCount))
        .andDo(print());
  }
}
