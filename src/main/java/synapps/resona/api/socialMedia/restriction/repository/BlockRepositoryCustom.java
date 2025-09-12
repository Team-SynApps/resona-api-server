package synapps.resona.api.socialMedia.restriction.repository;

import java.util.List;
import synapps.resona.api.socialMedia.restriction.dto.BlockedMemberResponse;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
