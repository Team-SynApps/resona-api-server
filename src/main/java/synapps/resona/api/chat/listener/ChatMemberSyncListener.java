package synapps.resona.api.chat.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.repository.ChatMemberRepository;
import synapps.resona.api.member.event.MemberUpdatedEvent;

@Component
@RequiredArgsConstructor
public class ChatMemberSyncListener {
  private final ChatMemberRepository chatMemberRepository;

  @TransactionalEventListener
  public void handleMemberUpdate(MemberUpdatedEvent event) {
    ChatMember chatMember = ChatMember.from(event.memberId(), event.profile());

    chatMemberRepository.save(chatMember);
  }
}
