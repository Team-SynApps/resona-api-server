package synapps.resona.api.mysql.socialMedia.controller.feed;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedWithMediaDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedRegistrationRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedMemberDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedReadResponse;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.mysql.socialMedia.dto.media.FeedMediaDto;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.media.FeedMedia;
import synapps.resona.api.mysql.socialMedia.service.feed.FeedService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = FeedController.class,
    excludeAutoConfiguration = {
//        SecurityAutoConfiguration.class,
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

  private FeedDto createFeedDto(Long id, String content, LocalDateTime createdAt) {
    FeedMemberDto memberDto = FeedMemberDto.of(100L, "test_user", "url");
    List<FeedMediaDto> images = List.of(
        FeedMediaDto.of(1L, "test-url1"),
        FeedMediaDto.of(2L, "test-url2"),
        FeedMediaDto.of(3L, "test-url3"));

    return FeedDto.builder()
        .feedId(id)
        .content(content)
        .member(memberDto)
        .images(images)
        .likeCount(5)
        .totalCommentCount(3)
        .createdAt(createdAt)
        .build();
  }

  @Test
  @DisplayName("피드 등록 성공 시, 등록된 FeedResponse를 반환한다")
  @WithMockUserPrincipal
  void registerFeed_success() throws Exception {
    // given
    FeedRegistrationRequest requestDto = new FeedRegistrationRequest(List.of(), new FeedRequest());
    FeedResponse responseDto = FeedResponse.builder().id("1").content("New Feed").build();

    given(feedService.registerFeed(anyList(), any(FeedRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(post("/feed")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.data.id").value("1"))
        .andExpect(jsonPath("$.data.content").value("New Feed"))
        .andDo(print());
  }

  @Test
  @DisplayName("피드 단건 조회 성공 시, FeedReadResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 100L)
  void readFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    FeedReadResponse responseDto = FeedReadResponse.builder().id("1").content("Feed Content").build();
    given(feedService.readFeed(feedId)).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(get("/feed/{feedId}", feedId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value("1"))
        .andExpect(jsonPath("$.data.content").value("Feed Content"))
        .andDo(print());
  }

  @Test
  @DisplayName("전체 피드 커서 기반 조회 성공 시, CursorResult를 포함한 SuccessResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 100L)
  void readFeedByCursor_success() throws Exception {
    // given
    LocalDateTime now = LocalDateTime.now();
    List<FeedDto> feedList = List.of(
        createFeedDto(2L, "Feed 2", now.minusHours(1)),
        createFeedDto(1L, "Feed 1", now.minusHours(2))
    );
    String nextCursor = now.minusHours(2).toString();
    CursorResult<FeedDto> cursorResult = new CursorResult<>(feedList, true, nextCursor);

    given(feedService.getFeedsByCursor(anyLong(), any(), anyInt())).willReturn(cursorResult);

    // when
    ResultActions actions = mockMvc.perform(get("/feeds")
        .param("size", "2")
        .with(csrf())
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

//  @Test
//  @DisplayName("특정 멤버의 피드 목록 조회 성공 시, FeedWithMediaDto 리스트를 반환한다")
//  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
//  void readFeedsByMember_success() throws Exception {
//    // given
//    Long memberId = 123L;
//    List<FeedWithMediaDto> responseList = List.of(new FeedWithMediaDto(1L, "Content 1", 10, List.of()));
//    given(feedService.getFeedsWithMediaAndLikeCount(memberId)).willReturn(responseList);
//
//    // when
//    ResultActions actions = mockMvc.perform(get("/feeds/member/{memberId}", memberId)
//        .contentType(MediaType.APPLICATION_JSON));
//
//    // then
//    actions.andExpect(status().isOk())
//        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data").isArray())
//        .andExpect(jsonPath("$.data[0].feedId").value(1L))
//        .andExpect(jsonPath("$.data[0].likeCount").value(10))
//        .andDo(print());
//  }

  @Test
  @DisplayName("피드 수정 성공 시, 수정된 FeedResponse를 반환한다")
  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
  void editFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    FeedUpdateRequest requestDto = new FeedUpdateRequest("Updated Content");
    FeedResponse responseDto = FeedResponse.builder().id("1").content("Updated Content").build();
    given(feedService.updateFeed(eq(feedId), any(FeedUpdateRequest.class))).willReturn(responseDto);

    // when
    ResultActions actions = mockMvc.perform(put("/feed/{feedId}", feedId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.id").value("1"))
        .andExpect(jsonPath("$.data.content").value("Updated Content"))
        .andDo(print());
  }

  @Test
  @DisplayName("피드 삭제 성공 시, 삭제 처리된 Feed 엔티티를 반환한다")
  @WithMockUserPrincipal(memberId = 1L, email = "test@example.com")
  void deleteFeed_success() throws Exception {
    // given
    Long feedId = 1L;
    Feed mockFeed = mock(Feed.class);
    given(mockFeed.getId()).willReturn(feedId);
    given(mockFeed.getContent()).willReturn("This feed is deleted.");
    given(feedService.deleteFeed(feedId)).willReturn(mockFeed);

    // when
    ResultActions actions = mockMvc.perform(delete("/feed/{feedId}", feedId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.data.id").value(feedId))
//        .andExpect(jsonPath("$.data.content").value("This feed is deleted."))
        .andDo(print());
  }
}