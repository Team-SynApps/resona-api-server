package synapps.resona.api.socialMedia.feed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.member.dto.MemberDto;
import synapps.resona.api.socialMedia.feed.dto.ScrapReadResponse;
import synapps.resona.api.socialMedia.feed.service.ScrapService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = ScrapController.class,
    excludeAutoConfiguration = { OAuth2ClientAutoConfiguration.class }
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

    mockScrapResponse = ScrapReadResponse.of(1L, 100L, LocalDateTime.now().toString());
  }

  @Test
  @WithMockUserPrincipal // 인증 정보 추가
  @DisplayName("스크랩 등록 성공 시, ScrapReadResponse를 반환한다")
  void registerScrap_success() throws Exception {
    // given
    Long feedId = 100L;
    given(scrapService.register(anyLong(), any(MemberDto.class))).willReturn(mockScrapResponse);

    // when
    ResultActions actions = mockMvc.perform(post("/scrap/{feedId}", feedId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data.scrapId").value(1L))
        .andExpect(jsonPath("$.data.feedId").value(100L))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal // 인증 정보 추가
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
  @WithMockUserPrincipal // 인증 정보 추가
  @DisplayName("스크랩 목록 커서 기반 조회 성공 시, CursorResult를 반환한다")
  void readScraps_success() throws Exception {
    // given
    String nextCursor = LocalDateTime.now().toString();
    List<ScrapReadResponse> scrapList = List.of(
        ScrapReadResponse.of(2L, 102L, LocalDateTime.now().toString()),
        ScrapReadResponse.of(1L, 101L, LocalDateTime.now().toString())
    );
    CursorResult<ScrapReadResponse> cursorResult = new CursorResult<>(scrapList, true, nextCursor);
    given(scrapService.readScrapsByCursor(any(), anyInt(), any(MemberDto.class))).willReturn(cursorResult);

    // when
    ResultActions actions = mockMvc.perform(get("/scraps")
        .param("size", "2")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.cursor").value(nextCursor))
        .andExpect(jsonPath("$.data.hasNext").value(true))
        .andExpect(jsonPath("$.data.values").isArray())
        .andExpect(jsonPath("$.data.values.length()").value(2))
        .andExpect(jsonPath("$.data.values[0].scrapId").value(2L))
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal // 인증 정보 추가
  @DisplayName("스크랩 취소 성공 시, 200 OK와 성공 메세지를 반환한다")
  void cancelScrap_success() throws Exception {
    // given
    Long scrapId = 1L;
    doNothing().when(scrapService).cancelScrap(scrapId, 1L);

    // when
    ResultActions actions = mockMvc.perform(delete("/scrap/{scrapId}", scrapId)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());

    verify(scrapService).cancelScrap(scrapId, 1L);
  }
}