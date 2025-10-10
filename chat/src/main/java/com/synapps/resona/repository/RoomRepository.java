package com.synapps.resona.repository;

import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@MongoDBRepository
public interface RoomRepository extends MongoRepository<ChatRoom, Long> {
  // 사용자가 특정 채팅방의 멤버인지 확인할 때 사용
  @Query(value = "{ '_id': ?0, 'memberIds': ?1 }", exists = true)
  boolean isMemberInRoom(Long roomId, Long memberId);
}