package synapps.resona.api.chat.dto;

import java.time.LocalDateTime;
import synapps.resona.api.chat.entity.ChatMessage;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.entity.MessageType;

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