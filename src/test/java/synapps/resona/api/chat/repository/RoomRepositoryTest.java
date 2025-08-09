package synapps.resona.api.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import synapps.resona.api.chat.entity.ChatRoom;

@ActiveProfiles("test")
@DataMongoTest
class RoomRepositoryTest {

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void tearDown() {
    // 각 테스트 후 DB 초기화
    mongoTemplate.getDb().drop();
  }

  @Test
  @DisplayName("isMemberInRoom: 사용자가 채팅방 멤버일 경우 true를 반환한다")
  void givenMemberInRoom_whenIsMemberInRoom_thenReturnsTrue() {
    // given
    Long roomId = 1L;
    Long memberId = 100L;
    ChatRoom room = ChatRoom.of(roomId, "테스트 채팅방", List.of(memberId, 101L, 102L));
    mongoTemplate.save(room);

    // when
    boolean result = roomRepository.isMemberInRoom(roomId, memberId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isMemberInRoom: 사용자가 채팅방 멤버가 아닐 경우 false를 반환한다")
  void givenMemberNotInRoom_whenIsMemberInRoom_thenReturnsFalse() {
    // given
    Long roomId = 1L;
    Long memberIdNotInRoom = 999L;
    ChatRoom room = ChatRoom.of(roomId, "테스트 채팅방", List.of(100L, 101L, 102L));
    mongoTemplate.save(room);

    // when
    boolean result = roomRepository.isMemberInRoom(roomId, memberIdNotInRoom);

    // then
    assertThat(result).isFalse();
  }
}