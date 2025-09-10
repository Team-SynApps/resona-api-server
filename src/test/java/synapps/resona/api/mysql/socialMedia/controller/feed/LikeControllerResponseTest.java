package synapps.resona.api.mysql.socialMedia.controller.feed;

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
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.controller.feed.LikeController;
import synapps.resona.api.socialMedia.dto.like.request.LikeRequest;
import synapps.resona.api.socialMedia.dto.like.response.LikeResponse;
import synapps.resona.api.socialMedia.service.feed.LikeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = LikeController.class,
    excludeAutoConfiguration = { OAuth2ClientAutoConfiguration.class }
)
@MockBean(JpaMetamodelMappingContext.class)
class LikeControllerResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LikeService likeService;

    @MockBean
    private ServerInfoConfig serverInfo;

    @BeforeEach
    void setUp() {
        given(serverInfo.getApiVersion()).willReturn("v1");
        given(serverInfo.getServerName()).willReturn("test-server");
    }

    @Test
    @DisplayName("좋아요 등록 성공 시, 등록된 좋아요 정보를 반환한다")
    @WithMockUserPrincipal
    void registerLike_success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        LikeResponse responseDto = LikeResponse.of(100L, memberId, feedId, LocalDateTime.now());

        given(likeService.register(anyLong(), anyLong())).willReturn(responseDto);

        // when
        ResultActions actions = mockMvc.perform(post("/likes/{feedId}", feedId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
            .andExpect(jsonPath("$.data.likeId").value(100L))
            .andExpect(jsonPath("$.data.memberId").value(memberId))
            .andExpect(jsonPath("$.data.feedId").value(feedId))
            .andDo(print());

        verify(likeService).register(feedId, memberId);
    }

    @Test
    @DisplayName("좋아요 취소 성공 시, 200 OK와 성공 메세지를 반환한다")
    @WithMockUserPrincipal
    void cancelLike_success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        doNothing().when(likeService).cancel(anyLong(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(delete("/likes/{feedId}", feedId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());

        verify(likeService).cancel(feedId, memberId);
    }
}
