package synapps.resona.api.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.chat.code.ChatErrorCode;
import synapps.resona.api.chat.code.ChatSuccessCode;
import synapps.resona.api.chat.dto.CreateRoomRequest;
import synapps.resona.api.chat.entity.ChatRoom;
import synapps.resona.api.chat.service.ChatRoomService;
import synapps.resona.api.global.annotation.ApiErrorSpec;
import synapps.resona.api.global.annotation.ApiSuccessResponse;
import synapps.resona.api.global.annotation.ErrorCodeSpec;
import synapps.resona.api.global.annotation.SuccessCodeSpec;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.RequestInfo;
import synapps.resona.api.global.dto.response.SuccessResponse;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Tag(name = "ChatRoom", description = "채팅방 생성 API")
@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "채팅방 생성", description = "새로운 1:1 또는 그룹 채팅방을 생성합니다. (인증 필요)")
  @ApiSuccessResponse(@SuccessCodeSpec(enumClass = ChatSuccessCode.class, code = "ROOM_CREATED_SUCCESS"))
  @ApiErrorSpec(@ErrorCodeSpec(enumClass = ChatErrorCode.class, codes = {"CANNOT_CREATE_ROOM_WITH_SELF"}))
  @PostMapping
  public ResponseEntity<SuccessResponse<ChatRoom>> createRoom(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody CreateRoomRequest request,
      HttpServletRequest httpServletRequest
  ) {
    Long creatorId = userPrincipal.getMemberId();

    ChatRoom createdRoom = chatRoomService.createChatRoom(
        creatorId,
        request.roomName(),
        request.memberIds()
    );

    return ResponseEntity
        .status(ChatSuccessCode.ROOM_CREATED_SUCCESS.getStatus())
        .body(SuccessResponse.of(
            ChatSuccessCode.ROOM_CREATED_SUCCESS,
            createRequestInfo(httpServletRequest.getRequestURI()),
            createdRoom
        ));
  }
}