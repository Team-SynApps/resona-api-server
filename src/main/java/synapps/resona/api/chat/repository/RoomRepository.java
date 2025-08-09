package synapps.resona.api.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import synapps.resona.api.chat.entity.ChatRoom;
import synapps.resona.api.global.annotation.DatabaseRepositories.MongoDBRepository;

@MongoDBRepository
public interface RoomRepository extends MongoRepository<ChatRoom, Long> {
  // 사용자가 특정 채팅방의 멤버인지 확인할 때 사용
  @Query(value = "{ '_id': ?0, 'memberIds': ?1 }", exists = true)
  boolean isMemberInRoom(Long roomId, Long memberId);
}