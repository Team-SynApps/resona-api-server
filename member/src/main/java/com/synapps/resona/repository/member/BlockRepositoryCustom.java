package com.synapps.resona.repository.member;


import com.synapps.resona.dto.BlockedMemberResponse;
import java.util.List;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
