package synapps.resona.api.mysql.socialMedia.controller.comment;

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
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentResponse;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyResponse;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
  @DisplayName("댓글 등록 성공 시, 등록된 CommentPostResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void registerComment_success() throws Exception {
    // given
    CommentRequest requestDto = new CommentRequest(1L, "New Comment");
    CommentResponse responseDto = CommentResponse.of(101L, "New Comment", LocalDateTime.now(), LocalDateTime.now());
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
        .andExpect(jsonPath("$.data.content").value("New Comment"))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 단건 조회 성공 시, CommentReadResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getComment_success() throws Exception {
    // given
    Long commentId = 101L;
    CommentResponse responseDto = CommentResponse.of(commentId, "A single comment", LocalDateTime.now(), LocalDateTime.now());
    given(commentService.getComment(commentId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/{commentId}", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentId").value(commentId))
        .andDo(print());
  }

  @Test
  @DisplayName("피드 전체 댓글 조회 성공 시, CommentPostResponse 리스트를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getComments_success() throws Exception {
    // given
    Long feedId = 1L;
    List<CommentResponse> responseList = List.of(
        CommentResponse.of(101L, "First comment", LocalDateTime.now(), LocalDateTime.now()),
        CommentResponse.of(102L, "Second comment", LocalDateTime.now(), LocalDateTime.now())
    );
    given(commentService.getCommentsByFeedId(1L, feedId)).willReturn(responseList);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/all/{feedId}", feedId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[1].commentId").value(102L))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글의 답글 조회 성공 시, ReplyReadResponse 리스트를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void getReplies_success() throws Exception {
    // given
    Long commentId = 101L;
    List<ReplyResponse> responseList = List.of(
        ReplyResponse.of(commentId,201L,  "This is a reply", LocalDateTime.now())
    );
    given(commentService.getReplies(commentId)).willReturn(responseList);

    // when
    ResultActions actions = mockMvc.perform(get("/comments/{commentId}/replies", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].replyId").value("201"))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 수정 성공 시, 수정된 CommentUpdateResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void editComment_success() throws Exception {
    // given
    Long commentId = 101L;
    CommentUpdateRequest requestDto = new CommentUpdateRequest(commentId, "Updated content");
    CommentResponse responseDto = CommentResponse.of(commentId, "Updated content", LocalDateTime.now(), LocalDateTime.now());
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
  @DisplayName("댓글 삭제 성공 시, 삭제 처리된 Comment 엔티티를 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void deleteComment_success() throws Exception {
    // given
    Long commentId = 101L;
    Comment mockComment = mock(Comment.class);
    given(mockComment.getId()).willReturn(commentId);
    given(mockComment.getContent()).willReturn("This comment was deleted.");
    given(commentService.deleteComment(commentId)).willReturn(mockComment);

    // when
    ResultActions actions = mockMvc.perform(delete("/comments/{commentId}", commentId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data[0].id").value(commentId))
//        .andExpect(jsonPath("$.data[0].content").value("This comment was deleted."))
        .andDo(print());
  }
}