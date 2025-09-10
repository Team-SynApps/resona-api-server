package synapps.resona.api.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import synapps.resona.api.chat.entity.ChatMessage;
import synapps.resona.api.chat.entity.MessageBucket;
import synapps.resona.api.config.TestContainerConfig;

@ActiveProfiles("test")
@DataMongoTest
@ImportTestcontainers(TestContainerConfig.class)
class MessageBucketRepositoryTest {

  @Autowired
  private MessageBucketRepository messageBucketRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void tearDown() {
    mongoTemplate.getDb().drop();
  }

  @Nested
  @DisplayName("findLatestAvailableBucket method 테스트")
  class FindLatestAvailableBucketTests {

    @Test
    @DisplayName("가득 차지 않은 가장 최신 버킷을 정확히 찾아낼 수 있다.")
    void shouldFindLatestBucketThatIsNotFull() {
      // given
      Long roomId = 1L;
      ChatMessage msg = ChatMessage.createTextMessage(100L, "test", LocalDateTime.now());

      // 1번 버킷 (가득 참)
      MessageBucket fullBucket = MessageBucket.createFirstBucket(roomId, 1, msg);
      for (int i = 0; i < 200; i++) fullBucket.addMessage(msg); // messageCount = 201
      mongoTemplate.save(fullBucket);

      // 2번 버킷 (가득 차지 않음)
      MessageBucket availableBucket = MessageBucket.createFirstBucket(roomId, 2, msg);
      mongoTemplate.save(availableBucket);

      // when
      MessageBucket result = messageBucketRepository.findLatestAvailableBucket(roomId, 200).orElse(null);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(availableBucket.getId());
      assertThat(result.getBucketSequence()).isEqualTo(2);
    }
  }

}