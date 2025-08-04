package synapps.resona.api.mysql.socialMedia.repository.restriction;

import static synapps.resona.api.mysql.member.entity.member.QMember.member;
import static synapps.resona.api.mysql.member.entity.profile.QProfile.profile;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.restriction.BlockedMemberResponse;


@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<BlockedMemberResponse> findBlockedMembers(Long memberId) {
    return queryFactory
        .select(Projections.constructor(BlockedMemberResponse.class,
            block.id,
            block.blocked.id,
            block.blocked.profile.nickname,
            block.blocked.profile.profileImageUrl))
        .from(block)
        .join(block.blocked, member)
        .join(member.profile, profile)
        .where(block.blocker.id.eq(memberId))
        .fetch();
  }

}
