package com.synapps.resona.restriction.repository;

import com.synapps.resona.restriction.dto.BlockedMemberResponse;
import java.util.List;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
