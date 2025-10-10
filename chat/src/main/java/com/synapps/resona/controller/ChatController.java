package com.synapps.resona.controller;

import com.synapps.resona.code.ChatErrorCode;
import com.synapps.resona.code.ChatSuccessCode;
import com.synapps.resona.dto.MessageDto;
import com.synapps.resona.dto.SendMessageRequest;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chat", description = "채팅 메시지 전송 및 조회 API")
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "채팅 메시지 전송", description = "특정 채팅방에 메시지를 전송합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = ChatSuccessCode.class, code = "MESSAGE_SENT_SUCCESS"))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = ChatErrorCode.class, codes = {"NOT_A_MEMBER"}))
  @PostMapping("/rooms/{roomId}/messages")
  public ResponseEntity<SuccessResponse<Void>> sendMessage(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long roomId,
      @RequestBody SendMessageRequest request,
      HttpServletRequest httpServletRequest) {

    chatService.sendMessage(user.getMemberId(), roomId, request);

    return ResponseEntity
        .status(ChatSuccessCode.MESSAGE_SENT_SUCCESS.getStatus())
        .body(SuccessResponse.of(ChatSuccessCode.MESSAGE_SENT_SUCCESS, createRequestInfo(httpServletRequest.getRequestURI())));
  }

  /**
   * TODO: Custom Swagger에서 Slice element에 대한 response를 추가해야 함.
   * @param roomId
   * @param cursor
   * @param httpServletRequest
   * @return
   */
  @Operation(summary = "채팅 메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다. (무한 스크롤 페이징)")
  @Parameter(name = "cursor", description = "마지막으로 조회된 메시지의 timestamp (ISO 8601 형식). 첫 조회 시에는 현재 시간")
//  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = ChatSuccessCode.class, code = "GET_MESSAGES_SUCCESS", sliceElementClass = MessageDto.class))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = ChatErrorCode.class, codes = {"ROOM_NOT_FOUND", "NOT_A_MEMBER", "SENDER_NOT_FOUND"}))
  @GetMapping("/rooms/{roomId}/messages")
  public ResponseEntity<SuccessResponse<Slice<MessageDto>>> getMessages(
      @PathVariable Long roomId,
      @RequestParam(required = false) LocalDateTime cursor,
      HttpServletRequest httpServletRequest) {

    LocalDateTime effectiveCursor = (cursor == null) ? LocalDateTime.now() : cursor;
    Slice<MessageDto> messages = chatService.getMessages(roomId, effectiveCursor);

    return ResponseEntity
        .status(ChatSuccessCode.GET_MESSAGES_SUCCESS.getStatus())
        .body(SuccessResponse.of(ChatSuccessCode.GET_MESSAGES_SUCCESS, createRequestInfo(httpServletRequest.getRequestURI()), messages));
  }
}