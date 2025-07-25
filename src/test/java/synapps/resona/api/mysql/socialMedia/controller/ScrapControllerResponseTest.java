package synapps.resona.api.mysql.socialMedia.controller;

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
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.socialMedia.controller.feed.ScrapController;
import synapps.resona.api.mysql.socialMedia.dto.scrap.ScrapReadResponse;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;
import synapps.resona.api.mysql.socialMedia.service.feed.ScrapService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = ScrapController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class ScrapControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ScrapService scrapService;

  @MockBean
  private ServerInfoConfig serverInfo;

  private ScrapReadResponse mockScrapResponse;

  @BeforeEach
  void setUp() {
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");

    // 테스트에서 공통으로 사용할 ScrapReadResponse DTO 생성
    mockScrapResponse = ScrapReadResponse.of(1L, 100L, LocalDateTime.now().toString());
  }

  @Test
  @DisplayName("스크랩 등록 성공 시, ScrapReadResponse를 반환한다")
  void registerScrap_success() throws Exception {
    // given
    Long feedId = 100L;
    given(scrapService.register(feedId)).willReturn(mockScrapResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/scrap/{feedId}", feedId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.scrapId").value(1L))
        .andExpect(jsonPath("$.data.feedId").value(100L))
        .andDo(print());
  }

  @Test
  @DisplayName("스크랩 단건 조회 성공 시, ScrapReadResponse를 반환한다")
  void readScrap_success() throws Exception {
    // given
    Long scrapId = 1L;
    given(scrapService.read(scrapId)).willReturn(mockScrapResponse);

    // when
    ResultActions actions = mockMvc.perform(get("/scrap/{scrapId}", scrapId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.scrapId").value(1L))
        .andExpect(jsonPath("$.data.feedId").value(100L))
        .andDo(print());
  }

  @Test
  @DisplayName("스크랩 목록 커서 기반 조회 성공 시, CursorResult를 반환한다")
  void readScraps_success() throws Exception {
    // given
    String nextCursor = LocalDateTime.now().toString();
    List<ScrapReadResponse> scrapList = List.of(
        ScrapReadResponse.of(2L, 102L, LocalDateTime.now().toString()),
        ScrapReadResponse.of(1L, 101L, LocalDateTime.now().toString())
    );
    CursorResult<ScrapReadResponse> cursorResult = new CursorResult<>(scrapList, true, nextCursor);
    given(scrapService.readScrapsByCursor(any(), anyInt())).willReturn(cursorResult);

    // when
    ResultActions actions = mockMvc.perform(get("/scraps")
        .param("size", "2")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.cursor").value(nextCursor))
        .andExpect(jsonPath("$.meta.hasNext").value(true))
        .andExpect(jsonPath("$.data.values").isArray())
        .andExpect(jsonPath("$.data.values.length()").value(2))
        .andExpect(jsonPath("$.data.values[0].scrapId").value(2L))
        .andDo(print());
  }

  @Test
  @DisplayName("스크랩 취소 성공 시, 200 OK와 성공 메세지를 반환한다")
  void cancelScrap_success() throws Exception {
    // given
    Long scrapId = 1L;
    // scrapService.cancelScrap은 void를 반환하므로 doNothing()으로 설정
    doNothing().when(scrapService).cancelScrap(scrapId);

    // when
    ResultActions actions = mockMvc.perform(delete("/scrap/{scrapId}", scrapId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").doesNotExist()) // 데이터가 없음을 확인
        .andDo(print());

    // scrapService.cancelScrap이 호출되었는지 검증
    verify(scrapService).cancelScrap(scrapId);
  }
}