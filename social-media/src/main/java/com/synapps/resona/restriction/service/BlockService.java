package com.synapps.resona.restriction.service;

import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.restriction.dto.BlockedMemberResponse;
import com.synapps.resona.restriction.entity.Block;
import com.synapps.resona.restriction.exception.BlockException;
import com.synapps.resona.restriction.repository.BlockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {

  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public void blockMember(Long blockedId, MemberDto memberInfo) {
    Member blocker = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member blocked = memberRepository.findById(blockedId)
        .orElseThrow(MemberException::memberNotFound);

    if (blocker.getId().equals(blocked.getId())) {
      throw BlockException.cannotBlockSelf();
    }

    if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
      throw BlockException.alreadyBlocked();
    }

    Block block = Block.of(blocker, blocked);
    blockRepository.save(block);
  }

  @Transactional
  public void unblockMember(Long blockedId, MemberDto memberInfo) {
    Member blocker = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member blocked = memberRepository.findById(blockedId)
        .orElseThrow(MemberException::memberNotFound);

    Block block = blockRepository.findByBlockerAndBlockedIncludeDeleted(blocker.getId(), blocked.getId())
        .orElseThrow(BlockException::notBlocked);

    if(!block.isDeleted()){
      throw BlockException.notBlocked();
    }

    block.softDelete();
  }


  @Transactional
  public List<BlockedMemberResponse> getBlockedMembers(MemberDto memberInfo) {
    return blockRepository.findBlockedMembers(memberInfo.getId());
  }
}