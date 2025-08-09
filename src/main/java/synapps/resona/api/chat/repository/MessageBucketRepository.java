package synapps.resona.api.chat.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import synapps.resona.api.chat.entity.MessageBucket;
import synapps.resona.api.global.annotation.DatabaseRepositories.MongoDBRepository;

@MongoDBRepository
public interface MessageBucketRepository extends MongoRepository<MessageBucket, String> {

  /**
   * 메시지를 추가할, 가장 최신의 (아직 가득 차지 않은) 버킷을 찾습니다.
   *
   * @param roomId 채팅방 ID
   * @param maxCount 버킷의 최대 메시지 수
   * @return MessageBucket Optional 객체
   */
  @Query(value = "{ 'roomId': ?0, 'messageCount': { '$lt': ?1 } }", sort = "{ 'bucketSequence' : -1 }")
  Optional<MessageBucket> findLatestAvailableBucket(Long roomId, int maxCount);

  /**
   * 특정 채팅방의 가장 마지막 버킷을 찾습니다. (새 버킷의 sequence를 정하기 위함)
   *
   * @param roomId 채팅방 ID
   * @return MessageBucket Optional 객체
   */
  @Query(value = "{ 'roomId': ?0 }", sort = "{ 'bucketSequence' : -1 }")
  Optional<MessageBucket> findLastBucket(Long roomId);

  /**
   * 특정 시간(커서) 이전의 메시지 목록을 페이징 조회합니다. (무한 스크롤)
   * Pageable 객체에 포함된 정렬 및 개수 제한(limit) 정보가 쿼리에 자동으로 적용됩니다.
   *
   * @param roomId 채팅방 ID
   * @param cursor 기준 시간
   * @param pageable 페이징 정보 (size, sort 등)
   * @return MessageBucket Slice 객체
   */
  @Query("{ 'roomId': ?0, 'startTime': { '$lt': ?1 } }")
  Slice<MessageBucket> findMessagesBefore(Long roomId, LocalDateTime cursor, Pageable pageable);
}