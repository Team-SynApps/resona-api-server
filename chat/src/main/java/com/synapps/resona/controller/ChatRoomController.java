package com.synapps.resona.controller;

import com.synapps.resona.code.ChatErrorCode;
import com.synapps.resona.code.ChatSuccessCode;
import com.synapps.resona.dto.CreateRoomRequest;
import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.annotation.ApiErrorSpec;
import com.synapps.resona.annotation.ApiSuccessResponse;
import com.synapps.resona.annotation.ErrorCodeSpec;
import com.synapps.resona.annotation.SuccessCodeSpec;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.service.ChatRoomService;
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

@Tag(name = "ChatRoom", description = "채팅방 생성 API")
@RestController
@RequestMapping("/chat/rooms")
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody CreateRoomRequest request,
      HttpServletRequest httpServletRequest
  ) {
    Long creatorId = user.getMemberId();

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