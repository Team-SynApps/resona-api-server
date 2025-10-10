package com.synapps.resona.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "message_buckets")
//@CompoundIndex(name = "room_bucket_idx", def = "{'roomId' : 1, 'bucket_sequence' : -1}")
public class MessageBucket {

  @Id
  private String id;

  private Long roomId;

  private int bucketSequence;

  private int messageCount;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private List<ChatMessage> messages = new ArrayList<>();


  private MessageBucket(Long roomId, int bucketSequence, ChatMessage firstMessage) {
    this.roomId = roomId;
    this.bucketSequence = bucketSequence;
    this.startTime = firstMessage.getTimestamp();
    this.endTime = firstMessage.getTimestamp();
    this.messages.add(firstMessage);
    this.messageCount = 1;
  }

  public static MessageBucket createFirstBucket(Long roomId, int bucketSequence, ChatMessage firstMessage) {
    return new MessageBucket(roomId, bucketSequence, firstMessage);
  }

  public void addMessage(ChatMessage message) {
    this.messages.add(message);
    this.messageCount++;
    this.endTime = message.getTimestamp();
  }
}