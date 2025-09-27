package com.synapps.resona.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * RabbitMQ로부터 수신한 채팅 메시지를 역직렬화하기 위한 DTO
 */
public record MessageDto(
    String msgId,
    Long roomId,
    String type,
    String content,
    LocalDateTime timestamp,
    Sender sender
) {
  public record Sender(
      Long id,
      String nickname,
      String profileImageUrl
  ) {
    @JsonCreator
    public Sender(
        @JsonProperty("id") Long id,
        @JsonProperty("nickname") String nickname,
        @JsonProperty("profileImageUrl") String profileImageUrl
    ) {
      this.id = id;
      this.nickname = nickname;
      this.profileImageUrl = profileImageUrl;
    }
  }

  @JsonCreator
  public MessageDto(
      @JsonProperty("msgId") String msgId,
      @JsonProperty("roomId") Long roomId,
      @JsonProperty("type") String type,
      @JsonProperty("content") String content,
      @JsonProperty("timestamp") LocalDateTime timestamp,
      @JsonProperty("sender") Sender sender
  ) {
    this.msgId = msgId;
    this.roomId = roomId;
    this.type = type;
    this.content = content;
    this.timestamp = timestamp;
    this.sender = sender;
  }
}