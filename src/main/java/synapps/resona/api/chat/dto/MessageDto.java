package synapps.resona.api.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import synapps.resona.api.chat.entity.ChatMessage;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.entity.MessageType;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

  private String msgId;
  private SenderDto sender;
  private MessageType type;
  private String content;
  private LocalDateTime timestamp;

  // 파일/이미지 관련 정보
  private String fileUrl;
  private String fileName;
  private Long fileSize;

  @Builder
  private MessageDto(String msgId, SenderDto sender, MessageType type, String content, LocalDateTime timestamp, String fileUrl, String fileName, Long fileSize) {
    this.msgId = msgId;
    this.sender = sender;
    this.type = type;
    this.content = content;
    this.timestamp = timestamp;
    this.fileUrl = fileUrl;
    this.fileName = fileName;
    this.fileSize = fileSize;
  }

  public static MessageDto of(ChatMessage message, ChatMember member) {
    return MessageDto.builder()
        .msgId(message.getMsgId().toHexString())
        .sender(SenderDto.from(member))
        .type(message.getType())
        .content(message.getContent())
        .timestamp(message.getTimestamp())
        .fileUrl(message.getFileUrl())
        .fileName(message.getFileName())
        .fileSize(message.getFileSize())
        .build();
  }
}