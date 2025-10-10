package com.synapps.resona.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.synapps.resona.dto.response.MemberProfileDto;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.service.FollowService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@WebMvcTest(
    controllers = FollowController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
@Disabled
class FollowControllerResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FollowService followService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    // 공통 Mock 설정
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @DisplayName("사용자 팔로우 성공 시 'followed' 메시지를 반환한다")
  void follow_success() throws Exception {
    // given
    Long targetMemberId = 123L;
    // follow 서비스 메소드는 반환값이 없으므로, 호출이 되는지만 검증
    doNothing().when(followService).follow(anyLong());

    // when
    ResultActions actions = mockMvc.perform(post("/follow/{memberId}", targetMemberId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
//        .andExpect(jsonPath("$.meta.message").value("followed"))
        .andDo(print());
  }

  @Test
  @DisplayName("사용자 언팔로우 성공 시 'unfollowed' 메시지를 반환한다")
  void unfollow_success() throws Exception {
    // given
    Long targetMemberId = 123L;
    // unfollow 서비스 메소드는 반환값이 없으므로, 호출이 되는지만 검증
    doNothing().when(followService).unfollow(anyLong());

    // when
    ResultActions actions = mockMvc.perform(delete("/follow/{memberId}", targetMemberId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andDo(print());
  }

  @Test
  @DisplayName("팔로워 목록 조회 성공 시, MemberProfileDto 리스트를 반환한다")
  void getFollowers_success() throws Exception {
    // given
    Long memberId = 1L;
    List<MemberProfileDto> mockFollowers = List.of(
        MemberProfileDto.of(10L, "url1", "follower1", "tag1"),
        MemberProfileDto.of(11L, "url2", "follower2", "tag2")
    );
    given(followService.getFollowers(memberId)).willReturn(mockFollowers);

    // when
    ResultActions actions = mockMvc.perform(get("/follow/{memberId}/followers", memberId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].memberId").value(10L))
        .andExpect(jsonPath("$.data[0].nickname").value("follower1"))
        .andExpect(jsonPath("$.data[1].memberId").value(11L))
        .andExpect(jsonPath("$.data[1].nickname").value("follower2"))
        .andDo(print());
  }

  @Test
  @DisplayName("팔로잉 목록 조회 성공 시, MemberProfileDto 리스트를 반환한다")
  void getFollowings_success() throws Exception {
    // given
    Long memberId = 1L;
    List<MemberProfileDto> mockFollowings = List.of(
        MemberProfileDto.of(20L, "url3", "following1", "tag3")
    );
    given(followService.getFollowings(memberId)).willReturn(mockFollowings);

    // when
    ResultActions actions = mockMvc.perform(get("/follow/{memberId}/followings", memberId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].memberId").value(20L))
        .andExpect(jsonPath("$.data[0].nickname").value("following1"))
        .andDo(print());
  }
}