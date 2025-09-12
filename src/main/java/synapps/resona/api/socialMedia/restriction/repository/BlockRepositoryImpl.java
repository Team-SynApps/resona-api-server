package synapps.resona.api.socialMedia.restriction.repository;

import static synapps.resona.api.member.entity.member.QMember.member;
import static synapps.resona.api.member.entity.profile.QProfile.profile;
import static synapps.resona.api.socialMedia.entity.restriction.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.restriction.dto.BlockedMemberResponse;


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
