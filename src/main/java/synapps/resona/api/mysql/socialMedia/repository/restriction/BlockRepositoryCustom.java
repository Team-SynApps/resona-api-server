package synapps.resona.api.mysql.socialMedia.repository.restriction;

import java.util.List;
import synapps.resona.api.mysql.socialMedia.dto.restriction.BlockedMemberResponse;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Block;

public interface BlockRepositoryCustom {

  List<BlockedMemberResponse> findBlockedMembers(Long memberId);
}
