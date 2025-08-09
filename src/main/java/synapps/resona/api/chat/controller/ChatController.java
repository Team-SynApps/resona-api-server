package synapps.resona.api.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import synapps.resona.api.chat.code.*;
import synapps.resona.api.chat.dto.*;
import synapps.resona.api.chat.service.ChatService;
import synapps.resona.api.global.annotation.*;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.oauth.entity.UserPrincipal;

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
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable Long roomId,
      @RequestBody SendMessageRequest request,
      HttpServletRequest httpServletRequest) {

    chatService.sendMessage(userPrincipal.getMemberId(), roomId, request);

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