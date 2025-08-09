package synapps.resona.api.chat.entity;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "rooms")
public class ChatRoom {
  @Id
  private Long id;

  private String roomName;

  private List<Long> memberIds;

  private ChatRoom(Long id, String roomName, List<Long> memberIds) {
    this.id = id;
    this.roomName = roomName;
    this.memberIds = memberIds;
  }

  public static ChatRoom of(Long id, String roomName, List<Long> memberIds) {
    return new ChatRoom(id, roomName, memberIds);
  }

  public void addMember(Long memberId) {
    if (!this.memberIds.contains(memberId)) {
      this.memberIds.add(memberId);
    }
  }
}
