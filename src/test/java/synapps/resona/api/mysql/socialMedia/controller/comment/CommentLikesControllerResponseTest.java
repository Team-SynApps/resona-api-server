package synapps.resona.api.mysql.socialMedia.controller.comment;

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
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentLikeResponse;
import synapps.resona.api.mysql.socialMedia.service.comment.CommentLikesService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = CommentLikesController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class CommentLikesControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommentLikesService commentLikesService;

  @MockBean
  private ServerInfoConfig serverInfo;

  private CommentLikeResponse mockCommentLikeResponse;

  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");

    // 테스트에서 공통으로 사용할 CommentLikeResponseDto 생성
    mockCommentLikeResponse = CommentLikeResponse.of(1L, 101L, 303L, LocalDateTime.now());
  }

  @Test
  @DisplayName("댓글 좋아요 등록 성공 시, 200 OK와 CommentLikeResponseDto를 반환한다")
  void registerCommentLike_success() throws Exception {
    // given
    CommentLikesRequest requestDto = new CommentLikesRequest();
    requestDto.setCommentId(303L);
    given(commentLikesService.register(any(CommentLikesRequest.class))).willReturn(mockCommentLikeResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/comment-like")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.commentLikeId").value(1L))
        .andExpect(jsonPath("$.data.memberId").value(101L))
        .andExpect(jsonPath("$.data.commentId").value(303L))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 좋아요 취소 성공 시, 200 OK와 성공 메세지를 반환한다")
  void cancelCommentLike_success() throws Exception {
    // given
    Long commentLikeId = 1L;
    doNothing().when(commentLikesService).cancel(anyLong());

    // when
    ResultActions actions = mockMvc.perform(delete("/comment-like/{commentLikeId}", commentLikeId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());

    // commentLikesService.cancel이 호출되었는지 검증
    verify(commentLikesService).cancel(commentLikeId);
  }
}