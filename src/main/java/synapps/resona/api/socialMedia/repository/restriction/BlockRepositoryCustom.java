package synapps.resona.api.socialMedia.repository.restriction;

import java.util.List;
import synapps.resona.api.socialMedia.dto.restriction.BlockedMemberResponse;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
