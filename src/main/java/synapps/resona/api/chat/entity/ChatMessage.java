package synapps.resona.api.chat.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage {

  private ObjectId msgId;

  private Long senderId;

  private MessageType type;

  private LocalDateTime timestamp;

  private String content;


  // TEXT를 제외한 나머지 파일이나, 이미지 관련 타입일 때 사용
  private String fileUrl;
  private String fileName;
  private Long fileSize;

  /**
   *  텍스트 메시지 생성
   */
  public static ChatMessage createTextMessage(Long senderId, String content) {
    return new ChatMessage(
        ObjectId.get(),
        senderId,
        MessageType.TEXT,
        LocalDateTime.now(),
        content,
        null,
        null,
        null
    );
  }

  /**
   * 이미지 메시지 생성
   */
  public static ChatMessage createImageMessage(Long senderId, String fileUrl, String fileName, Long fileSize, String caption) {
    return new ChatMessage(
        ObjectId.get(),
        senderId,
        MessageType.IMAGE,
        LocalDateTime.now(),
        caption,
        fileUrl,
        fileName,
        fileSize
    );
  }
}
