package com.synapps.resona.service;

import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.query.member.event.MemberBlockedEvent;
import com.synapps.resona.query.member.event.MemberUnblockedEvent;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.dto.BlockedMemberResponse;
import com.synapps.resona.entity.member.Block;
import com.synapps.resona.exception.BlockException;
import com.synapps.resona.repository.member.BlockRepository;
import java.util.List;
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


  @Transactional
  public List<BlockedMemberResponse> getBlockedMembers(MemberDto memberInfo) {
    return blockRepository.findBlockedMembers(memberInfo.getId());
  }
}