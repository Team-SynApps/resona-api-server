package synapps.resona.api.chat.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.global.annotation.DatabaseRepositories.MongoDBRepository;

@MongoDBRepository
public interface ChatMemberRepository extends MongoRepository<ChatMember, Long> {
  // 메시지 보낸 사람 정보를 한 번에 조회하기 위한 메서드
  List<ChatMember> findAllByIdIn(Collection<Long> ids);
}
