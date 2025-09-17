package synapps.resona.api.socialMedia.restriction.controller;

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
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.config.WithMockUserPrincipal;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.socialMedia.restriction.dto.BlockedMemberResponse;
import synapps.resona.api.socialMedia.restriction.controller.BlockController;
import synapps.resona.api.socialMedia.restriction.service.BlockService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = BlockController.class,
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class BlockControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BlockService blockService;

  @MockBean
  private ServerInfoConfig serverInfo;

  @BeforeEach
  void setUp() {
    // 공통 Mock 설정
    given(serverInfo.getApiVersion()).willReturn("v1");
    given(serverInfo.getServerName()).willReturn("test-server");
  }

  @Test
  @WithMockUserPrincipal // 현재 인증된 사용자는 memberId 1L
  @DisplayName("사용자 차단 성공 시, 201 Created와 성공 메시지를 반환한다")
  void blockMember_success() throws Exception {
    // given
    long memberIdToBlock = 2L;
    doNothing().when(blockService).blockMember(anyLong(), any(MemberDto.class));

    // when
    ResultActions actions = mockMvc.perform(post("/member/{memberId}/block", memberIdToBlock)
        .with(csrf()));

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.meta.status").value(201))
        .andExpect(jsonPath("$.meta.message").value("User blocked successfully."))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal
  @DisplayName("사용자 차단 해제 성공 시, 200 OK와 성공 메시지를 반환한다")
  void unblockMember_success() throws Exception {
    // given
    long memberIdToUnblock = 2L;
    doNothing().when(blockService).unblockMember(anyLong(), any(MemberDto.class));

    // when
    ResultActions actions = mockMvc.perform(delete("/member/{memberId}/unblock", memberIdToUnblock)
        .with(csrf()));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.message").value("User unblocked successfully."))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());
  }

  @Test
  @WithMockUserPrincipal
  @DisplayName("차단한 사용자 목록 조회 성공 시, 200 OK와 사용자 목록을 반환한다")
  void readBlockedMembers_success() throws Exception {
    // given
    List<BlockedMemberResponse> mockResponse = List.of(
        BlockedMemberResponse.of(2L, 10L,"blockedUser1", "url1"),
        BlockedMemberResponse.of(3L, 11L, "blockedUser2", "url2")
    );
    given(blockService.getBlockedMembers(any(MemberDto.class))).willReturn(mockResponse);

    // when
    ResultActions actions = mockMvc.perform(get("/member/my-blocks")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.status").value(200))
        .andExpect(jsonPath("$.meta.message").value("Successfully retrieved blocked user list."))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].blockId").value(2L))
        .andExpect(jsonPath("$.data[0].memberId").value(10L))
        .andExpect(jsonPath("$.data[0].nickname").value("blockedUser1"))
        .andExpect(jsonPath("$.data[1].blockId").value(3L))
        .andExpect(jsonPath("$.data[1].memberId").value(11L))
        .andExpect(jsonPath("$.data[1].nickname").value("blockedUser2"))
        .andDo(print());
  }
}