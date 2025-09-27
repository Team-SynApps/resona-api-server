package com.synapps.resona.restriction.repository;

import static com.synapps.resona.entity.member.QMember.member;
import static com.synapps.resona.entity.profile.QProfile.profile;
import static com.synapps.resona.restriction.entity.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.restriction.dto.BlockedMemberResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;


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
