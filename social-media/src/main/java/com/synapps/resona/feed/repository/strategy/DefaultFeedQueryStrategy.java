package com.synapps.resona.feed.repository.strategy;

import static com.synapps.resona.entity.member.QMember.member;
import static com.synapps.resona.entity.profile.QProfile.profile;
import static com.synapps.resona.feed.entity.QFeed.feed;
import static com.synapps.resona.restriction.entity.QBlock.block;
import static com.synapps.resona.restriction.entity.QFeedHide.feedHide;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.synapps.resona.feed.dto.FeedDetailDto;
import com.synapps.resona.feed.dto.FeedDto;
import com.synapps.resona.feed.dto.FeedMetaData;
import com.synapps.resona.feed.dto.condition.DefaultFeedSearchCondition;
import com.synapps.resona.feed.dto.request.FeedQueryRequest;
import com.synapps.resona.feed.repository.dsl.FeedExpressions;
import com.synapps.resona.feed.repository.dsl.FeedOrderSpecifier;
import com.synapps.resona.feed.repository.dsl.FeedPredicate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultFeedQueryStrategy implements FeedQueryStrategy<DefaultFeedSearchCondition> {
  private final JPAQueryFactory queryFactory;
  private final FeedExpressions feedExpressions;

  @Override
  public List<FeedDto> findFeeds(DefaultFeedSearchCondition condition, LocalDateTime cursor, Pageable pageable, Long viewerId) {
    // 대상 feed의 ID 목록을 먼저 조회
    List<Long> ids = queryFactory
        .select(feed.id)
        .from(feed)
        .join(feed.member, member)
        .leftJoin(block).on(
            block.blocker.id.eq(condition.getMemberId())
                    .and(block.blocked.id.eq(feed.member.id))
        )
        .leftJoin(feedHide).on(
            feedHide.feed.id.eq(feed.id)
                    .and(feedHide.member.id.eq(condition.getMemberId()))
        )
        .where(
            block.id.isNull(),      // 내가 차단한 사용자의 피드가 아님
            feedHide.id.isNull(),   // 내가 숨김 처리한 피드가 아님
            FeedPredicate.createdAtBefore(cursor)
        )
        .orderBy(FeedOrderSpecifier.getOrderBySpecifiers(condition.getSortBy()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    if (ids.isEmpty()) {
      return List.of();
    }

    // ID 목록으로 실제 데이터(카운트 포함)를 조회
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
    return DefaultFeedSearchCondition.class.isAssignableFrom(requestClass);
  }
}