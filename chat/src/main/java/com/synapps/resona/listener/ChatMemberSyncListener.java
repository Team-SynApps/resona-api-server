package com.synapps.resona.listener;

import com.synapps.resona.entity.ChatMember;
import com.synapps.resona.event.MemberUpdatedEvent;
import com.synapps.resona.repository.ChatMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMemberSyncListener {
  private final ChatMemberRepository chatMemberRepository;

  @TransactionalEventListener
  public void handleMemberUpdate(MemberUpdatedEvent event) {
    chatMemberRepository.findById(event.memberId()).ifPresentOrElse(
        chatMember -> chatMember.updateProfile(event.nickname(), event.profileImageUrl()),
        () -> {
          ChatMember newChatMember = ChatMember.of(event.memberId(), event.nickname(), event.profileImageUrl());
          chatMemberRepository.save(newChatMember);
        }
    );
  }
}
