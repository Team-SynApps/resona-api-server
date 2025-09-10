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
import synapps.resona.api.chat.entity.ChatMember;

@ActiveProfiles("test")
@DataMongoTest
class ChatMemberRepositoryTest {

  @Autowired
  private ChatMemberRepository chatMemberRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void tearDown() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @DisplayName("findAllByIdIn: 주어진 ID 목록에 해당하는 모든 멤버를 조회한다")
  void shouldFindAllMembersByIdIn() {
    // given
    ChatMember member1 = ChatMember.of(1L, "user1", "url1");
    ChatMember member2 = ChatMember.of(2L, "user2", "url2");
    ChatMember member3 = ChatMember.of(3L, "user3", "url3");
    mongoTemplate.save(member1);
    mongoTemplate.save(member2);
    mongoTemplate.save(member3);

    // when
    List<ChatMember> result = chatMemberRepository.findAllByIdIn(List.of(1L, 3L));

    // then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(ChatMember::getId).containsExactlyInAnyOrder(1L, 3L);
  }
}