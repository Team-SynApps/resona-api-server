package com.synapps.resona.domain.repository.feed.strategy;

import static com.synapps.resona.entity.member.QMember.member;
import static com.synapps.resona.entity.profile.QProfile.profile;
import static com.synapps.resona.feed.entity.QFeed.feed;
import static com.synapps.resona.restriction.entity.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.query.dto.feed.FeedDetailDto;
import com.synapps.resona.query.dto.feed.FeedDto;
import com.synapps.resona.query.dto.feed.FeedMetaData;
import com.synapps.resona.query.dto.feed.condition.MemberFeedSearchCondition;
import com.synapps.resona.query.dto.feed.request.FeedQueryRequest;
import com.synapps.resona.domain.repository.feed.dsl.FeedExpressions;
import com.synapps.resona.domain.repository.feed.dsl.FeedOrderSpecifier;
import com.synapps.resona.domain.repository.feed.dsl.FeedPredicate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class MemberFeedQueryStrategy implements FeedQueryStrategy<MemberFeedSearchCondition> {
  private final JPAQueryFactory queryFactory;
  private final FeedExpressions feedExpressions;

  @Override
  public List<FeedDto> findFeeds(MemberFeedSearchCondition condition, LocalDateTime cursor, Pageable pageable, Long viewerId) {
    List<Long> ids = queryFactory
        .select(feed.id)
        .from(feed)
        .leftJoin(block).on(
            block.blocker.id.eq(condition.getTargetMemberId())
                .and(block.blocked.id.eq(feed.member.id))
        )
        .where(
            block.id.isNull(),
            feed.member.id.eq(condition.getTargetMemberId()), // 특정 사용자의 피드만
            FeedPredicate.createdAtBefore(cursor)
        )
        .orderBy(FeedOrderSpecifier.getOrderBySpecifiers(condition.getSortBy()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    if (ids.isEmpty()) {
      return List.of();
    }

    List<FeedDetailDto> feedDetails = queryFactory
        .select(Projections.constructor(FeedDetailDto.class,
            feed,
            Projections.constructor(FeedMetaData.class,
                feedExpressions.getLikeCount(feed),
                feedExpressions.getCommentCount(feed),
                feedExpressions.isLiked(feed, viewerId),
                feedExpressions.isScraped(feed, viewerId)
            )
        ))
        .from(feed)
        .join(feed.member, member).fetchJoin()
        .join(member.profile, profile).fetchJoin()
        .where(feed.id.in(ids))
        .orderBy(FeedOrderSpecifier.getOrderBySpecifiers(condition.getSortBy()))
        .fetch();

    return feedDetails.stream()
        .map(FeedDto::from)
        .toList();
  }

  @Override
  public boolean supports(Class<? extends FeedQueryRequest> requestClass) {
    return MemberFeedSearchCondition.class.isAssignableFrom(requestClass);
  }
}