package com.synapps.resona.domain.repository.restriction;

import com.synapps.resona.query.dto.restriction.BlockedMemberResponse;
import java.util.List;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
