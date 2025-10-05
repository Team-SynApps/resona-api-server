package com.synapps.resona.repository.member;

import static com.synapps.resona.entity.member.QMember.member;
import static com.synapps.resona.entity.profile.QProfile.profile;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.dto.BlockedMemberResponse;
import com.synapps.resona.entity.member.QBlock;
import java.util.List;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<BlockedMemberResponse> findBlockedMembers(Long memberId) {
    return queryFactory
        .select(Projections.constructor(BlockedMemberResponse.class,
            QBlock.block.id,
            QBlock.block.blocked.id,
            QBlock.block.blocked.profile.nickname,
            QBlock.block.blocked.profile.profileImageUrl))
        .from(QBlock.block)
        .join(QBlock.block.blocked, member)
        .join(member.profile, profile)
        .where(QBlock.block.blocker.id.eq(memberId))
        .fetch();
  }

}
