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
import synapps.resona.api.mysql.socialMedia.controller.feed.LikeController;
import synapps.resona.api.mysql.socialMedia.dto.like.request.LikeRequest;
import synapps.resona.api.mysql.socialMedia.dto.like.response.LikeResponse;
import synapps.resona.api.mysql.socialMedia.service.feed.LikeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = LikeController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
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
    void registerLike_success() throws Exception {
        // given
        LikeRequest requestDto = new LikeRequest(1L);
        LikeResponse responseDto = LikeResponse.of(100L, 1L, 1L, LocalDateTime.now());

        given(likeService.register(any(LikeRequest.class), any(MemberDto.class))).willReturn(responseDto);

        // when
        ResultActions actions = mockMvc.perform(post("/like")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
            .andExpect(jsonPath("$.data.likeId").value(100L))
            .andExpect(jsonPath("$.data.memberId").value(1L))
            .andExpect(jsonPath("$.data.feedId").value(1L))
            .andDo(print());
    }

    @Test
    @DisplayName("좋아요 취소 성공 시, 200 OK와 성공 메세지를 반환한다")
    void cancelLike_success() throws Exception {
        // given
        Long likeId = 1L;
        // likeService.cancel의 반환 타입이 void이므로 doNothing()을 사용
        doNothing().when(likeService).cancel(likeId);

        // when
        ResultActions actions = mockMvc.perform(delete("/like/{likeId}", likeId)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());

        // likeService.cancel(likeId)가 호출되었는지 검증
        verify(likeService).cancel(likeId);
    }
}
