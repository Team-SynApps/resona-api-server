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
import synapps.resona.api.mysql.socialMedia.controller.feed.LikeController;
import synapps.resona.api.mysql.socialMedia.dto.feed.LikeRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Likes;
import synapps.resona.api.mysql.socialMedia.service.feed.LikeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
    @DisplayName("좋아요 등록 성공 시, 등록된 Likes 엔티티를 반환한다")
    void registerLike_success() throws Exception {
        // given
        LikeRequest requestDto = new LikeRequest(1L);
        Likes mockLike = mock(Likes.class);

        given(mockLike.getId()).willReturn(100L);
        given(likeService.register(any(LikeRequest.class))).willReturn(mockLike);

        // when
        ResultActions actions = mockMvc.perform(post("/like")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
            .andExpect(jsonPath("$.data.id").value(100L))
            .andDo(print());
    }

    @Test
    @DisplayName("좋아요 취소 성공 시, 취소 처리된 Likes 엔티티를 반환한다")
    void cancelLike_success() throws Exception {
        // given
        Long likeId = 1L;
        Likes mockLike = mock(Likes.class);

        given(mockLike.getId()).willReturn(likeId);
        given(likeService.cancel(likeId)).willReturn(mockLike);

        // when
        ResultActions actions = mockMvc.perform(delete("/like/{likeId}", likeId)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.status").value(200))
//            .andExpect(jsonPath("$.data.id").value(likeId))
            .andDo(print());
    }
}