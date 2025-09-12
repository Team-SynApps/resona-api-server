package synapps.resona.api.socialMedia.restriction.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.restriction.dto.BlockedMemberResponse;
import synapps.resona.api.socialMedia.restriction.entity.Block;
import synapps.resona.api.socialMedia.restriction.exception.BlockException;
import synapps.resona.api.socialMedia.restriction.repository.BlockRepository;

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