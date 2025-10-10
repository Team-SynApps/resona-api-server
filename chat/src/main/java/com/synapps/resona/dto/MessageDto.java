package com.synapps.resona.dto;

import com.synapps.resona.entity.ChatMember;
import com.synapps.resona.entity.ChatMessage;
import com.synapps.resona.entity.MessageType;
import java.time.LocalDateTime;

public record MessageDto(
    String msgId,
    Long roomId,
    MessageType type,
    String content,
    LocalDateTime timestamp,
    Sender sender
) {
  public record Sender(
      Long id,
      String nickname,
      String profileImageUrl
  ) {
    public static Sender from(ChatMember member) {
      return new Sender(member.getId(), member.getNickname(), member.getProfileImageUrl());
    }
  }

  public static MessageDto of(ChatMessage message, ChatMember member, Long roomId) {
    return new MessageDto(
        message.getMsgId().toString(),
        roomId,
        message.getType(),
        message.getContent(),
        message.getTimestamp(),
        Sender.from(member)
    );
  }
}