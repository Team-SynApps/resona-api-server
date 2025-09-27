package com.synapps.resona.controller;

import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.matching.code.MatchingErrorCode;
import com.synapps.resona.matching.code.MatchingSuccessCode;
import com.synapps.resona.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
      @AuthenticationPrincipal AuthenticatedUser user,
      HttpServletRequest httpServletRequest
  ) {
    Long requesterId = user.getMemberId();
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