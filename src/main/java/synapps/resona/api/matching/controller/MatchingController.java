package synapps.resona.api.matching.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.chat.entity.ChatRoom;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.matching.code.MatchingErrorCode;
import synapps.resona.api.matching.code.MatchingSuccessCode;
import synapps.resona.api.matching.service.MatchingService;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "Matching", description = "사용자 매칭 및 채팅방 자동 생성 API")
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchingController {

  private final MatchingService matchingService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "매칭 요청 및 채팅방 생성", description = "매칭을 요청하고, 성공 시 자동으로 채팅방을 생성합니다.")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = MatchingSuccessCode.class, code = "MATCHING_SUCCESS"))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = MatchingErrorCode.class, codes = {"MATCHING_FAILED"}))
  @PostMapping
  public ResponseEntity<SuccessResponse<ChatRoom>> requestMatch(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      HttpServletRequest httpServletRequest
  ) {
    Long requesterId = userPrincipal.getMemberId();
    ChatRoom createdRoom = matchingService.processMatchAndCreateRoom(requesterId);

    return ResponseEntity
        .status(MatchingSuccessCode.MATCHING_SUCCESS.getStatus())
        .body(SuccessResponse.of(
            MatchingSuccessCode.MATCHING_SUCCESS,
            createRequestInfo(httpServletRequest.getRequestURI()),
            createdRoom
        ));
  }
}