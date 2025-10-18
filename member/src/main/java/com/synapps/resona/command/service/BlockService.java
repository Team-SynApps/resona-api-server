package com.synapps.resona.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.query.event.MemberUnblockedEvent;
import com.synapps.resona.command.entity.member.Block;
import com.synapps.resona.exception.BlockException;
import com.synapps.resona.command.repository.member.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {

  private final BlockRepository blockRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void blockMember(Long blockerId, Long blockedId) {
    if (blockerId.equals(blockedId)) {
      throw BlockException.cannotBlockSelf();
    }

    Member blocker = memberService.getMember(blockerId);
    Member blocked = memberService.getMember(blockedId);

    if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
      throw BlockException.alreadyBlocked();
    }

    Block block = Block.of(blocker, blocked);
    blockRepository.save(block);

    eventPublisher.publishEvent(new MemberBlockedEvent(blockerId, blockedId));
  }

  @Transactional
  public void unblockMember(Long blockerId, Long unblockedId) {
    Member blocker = memberService.getMember(blockerId);
    Member blocked = memberService.getMember(unblockedId);

    Block block = blockRepository.findByBlockerAndBlockedIncludeDeleted(blocker.getId(), blocked.getId())
        .orElseThrow(BlockException::notBlocked);

    if(!block.isDeleted()){
      throw BlockException.notBlocked();
    }

    block.softDelete();
    eventPublisher.publishEvent(new MemberUnblockedEvent(blockerId, unblockedId));
  }
}