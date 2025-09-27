package com.synapps.resona.service;

import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.exception.ChatException;
import com.synapps.resona.repository.RoomRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final RoomRepository roomRepository;
  private final SequenceGeneratorService sequenceGenerator;

  @Transactional
  public ChatRoom createChatRoom(Long creatorId, String roomName, List<Long> inviteeIds) {
    // 자기 자신만 있는 채팅방은 생성 불가
    if (inviteeIds == null || inviteeIds.isEmpty()) {
      throw ChatException.cannotCreateRoomWithSelf();
    }


    Set<Long> memberIdSet = new HashSet<>(inviteeIds);
    memberIdSet.add(creatorId);
    List<Long> finalMemberIds = new ArrayList<>(memberIdSet);

    long roomId = sequenceGenerator.generateSequence(ChatRoom.SEQUENCE_NAME);

    ChatRoom newRoom = ChatRoom.of(roomId, roomName, finalMemberIds);

    return roomRepository.save(newRoom);
  }
}