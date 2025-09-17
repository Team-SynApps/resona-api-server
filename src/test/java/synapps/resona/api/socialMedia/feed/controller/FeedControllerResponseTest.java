package synapps.resona.api.socialMedia.feed.controller;

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
import synapps.resona.api.fixture.FeedFixture;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.socialMedia.feed.dto.FeedDto;
import synapps.resona.api.socialMedia.feed.dto.request.FeedRegistrationRequest;
import synapps.resona.api.socialMedia.feed.dto.request.FeedRequest;
import synapps.resona.api.socialMedia.feed.dto.request.FeedUpdateRequest;
import synapps.resona.api.socialMedia.feed.service.FeedService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static synapps.resona.api.fixture.FeedFixture.*;

@WebMvcTest(
    controllers = FeedController.class,
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class FeedControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private FeedService feedService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("피드 등록 성공 시, 등록된 FeedDto의 모든 필드를 정확히 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void registerFeed_success() throws Exception {
    // given
    FeedRegistrationRequest requestDto = createFeedRegistrationRequest();
    // given: 상세한 필드 값을 포함하는 DTO Mocking (신규 피드는 like, comment가 0)
    FeedDto responseDto = FeedFixture.createFeedDtoWithCounts(1L, 1L, "New Feed", LocalDateTime.now(), 0, 0, false, false);

    given(feedService.registerFeed(anyList(), any(FeedRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/feed")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then: 주요 필드들을 모두 검증
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.feedId").value(1L))
        .andExpect(jsonPath("$.data.content").value("New Feed"))
        .andExpect(jsonPath("$.data.likeCount").value(0))
        .andExpect(jsonPath("$.data.commentCount").value(0))
        .andExpect(jsonPath("$.data.hasLiked").value(false))
        .andExpect(jsonPath("$.data.hasScraped").value(false))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andExpect(jsonPath("$.data.author.memberId").value(1L))
        .andExpect(jsonPath("$.data.author.nickname").value("AuthorNickname"))
        .andExpect(jsonPath("$.data.images").isArray())
        .andDo(print());
  }

  @Test
  @DisplayName("피드 단건 조회 성공 시, FeedDto를 반환한다")
  @WithMockUserPrincipal(memberId = 100L) // 현재 로그인한 사용자 ID: 100
  void readFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    Long authorId = 10L;
    Long viewerId = 100L;

    FeedDto responseDto = FeedFixture.createFeedDto(feedId, authorId, "Feed Content", LocalDateTime.now().minusHours(1));

    given(feedService.readFeed(feedId, viewerId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/feed/{feedId}", feedId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.message").value("Successfully retrieved feed."))
        .andExpect(jsonPath("$.data.feedId").value(feedId))
        .andExpect(jsonPath("$.data.content").value("Feed Content"))
        .andExpect(jsonPath("$.data.likeCount").value(10))
        .andExpect(jsonPath("$.data.commentCount").value(5))
        .andExpect(jsonPath("$.data.hasLiked").value(true))
        .andExpect(jsonPath("$.data.hasScraped").value(false))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andExpect(jsonPath("$.data.author.memberId").value(authorId))
        .andExpect(jsonPath("$.data.author.nickname").value("AuthorNickname"))
        .andExpect(jsonPath("$.data.author.profileImageUrl").exists())
        .andExpect(jsonPath("$.data.images").isArray())
        .andExpect(jsonPath("$.data.images[0].url").value("http://image.url/1"))
        .andDo(print());
  }

  @Test
  @DisplayName("전체 피드 커서 기반 조회 성공 시, CursorResult<FeedDto>를 포함한 응답을 반환한다")
  @WithMockUserPrincipal(memberId = 100L)
  void readFeedByCursor_success() throws Exception {
    // given
    Long viewerId = 100L;
    LocalDateTime now = LocalDateTime.now();
    List<FeedDto> feedList = List.of(
        createFeedDto(2L, 20L, "Feed 2", now.minusHours(1)),
        createFeedDto(1L, 10L, "Feed 1", now.minusHours(2))
    );
    String nextCursor = feedList.get(feedList.size() - 1).getCreatedAt().toString();
    CursorResult<FeedDto> cursorResult = new CursorResult<>(feedList, true, nextCursor);

    given(feedService.getFeedsByCursor(eq(viewerId), any(), anyInt())).willReturn(cursorResult);

    // when
    ResultActions actions = mockMvc.perform(get("/feeds")
        .param("size", "2")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.cursor").value(nextCursor))
        .andExpect(jsonPath("$.meta.hasNext").value(true))
        .andExpect(jsonPath("$.data.values").isArray())
        .andExpect(jsonPath("$.data.values.length()").value(2))
        .andExpect(jsonPath("$.data.values[0].feedId").value(2))
        .andExpect(jsonPath("$.data.values[0].content").value("Feed 2"))
        .andDo(print());
  }

  @Test
  @DisplayName("피드 수정 성공 시, 수정된 FeedDto의 모든 필드를 정확히 반환한다")
  @WithMockUserPrincipal(memberId = 1L)
  void editFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    Long memberId = 1L;
    FeedUpdateRequest requestDto = createFeedUpdateRequest("Updated Content");
    // given: 수정 후의 상태를 반영한 상세한 DTO Mocking (좋아요, 댓글 등은 유지)
    FeedDto responseDto = FeedFixture.createFeedDtoWithCounts(feedId, memberId, "Updated Content", LocalDateTime.now(), 15, 7, true, true);

    given(feedService.updateFeed(eq(feedId), eq(memberId), any(FeedUpdateRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/feed/{feedId}", feedId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then: 주요 필드들을 모두 검증
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.feedId").value(feedId))
        .andExpect(jsonPath("$.data.content").value("Updated Content"))
        .andExpect(jsonPath("$.data.likeCount").value(15))
        .andExpect(jsonPath("$.data.commentCount").value(7))
        .andExpect(jsonPath("$.data.hasLiked").value(true))
        .andExpect(jsonPath("$.data.hasScraped").value(true))
        .andExpect(jsonPath("$.data.author.memberId").value(memberId))
        .andDo(print());
  }

  @Test
  @DisplayName("피드 삭제 성공 시, 200 OK 상태 코드와 성공 메시지를 반환한다")
  @WithMockUserPrincipal
  void deleteFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    doNothing().when(feedService).deleteFeed(anyLong());

    // when
    ResultActions actions = mockMvc.perform(delete("/feed/{feedId}", feedId)
        .with(csrf()));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.message").value("Feed deleted successfully."))
        // void 응답이므로 data 필드는 null 이거나 존재하지 않음
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());
  }
}