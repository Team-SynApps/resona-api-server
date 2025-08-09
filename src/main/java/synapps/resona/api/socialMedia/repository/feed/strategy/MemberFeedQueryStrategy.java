package synapps.resona.api.socialMedia.repository.feed.strategy;

import static synapps.resona.api.member.entity.member.QMember.member;
import static synapps.resona.api.member.entity.profile.QProfile.profile;
import static synapps.resona.api.socialMedia.entity.feed.QFeed.feed;
import static synapps.resona.api.socialMedia.entity.feed.QLikes.likes;
import static synapps.resona.api.socialMedia.entity.comment.QComment.comment;
import static synapps.resona.api.socialMedia.entity.restriction.QBlock.block;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import synapps.resona.api.socialMedia.dto.feed.condition.MemberFeedSearchCondition;
import synapps.resona.api.socialMedia.dto.feed.request.FeedQueryRequest;
import synapps.resona.api.socialMedia.dto.feed.response.FeedDto;
import synapps.resona.api.socialMedia.dto.feed.response.FeedWithCountsDto;
import synapps.resona.api.socialMedia.repository.feed.dsl.FeedOrderSpecifier;
import synapps.resona.api.socialMedia.repository.feed.dsl.FeedPredicate;


@Repository
@RequiredArgsConstructor
public class MemberFeedQueryStrategy implements FeedQueryStrategy<MemberFeedSearchCondition> {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<FeedDto> findFeeds(MemberFeedSearchCondition condition, LocalDateTime cursor, Pageable pageable) {
    // 특정 사용자의 피드만 조회하므로 차단/숨김 로직은 제외 (단, 정책에 따라 추가 가능)
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

    List<FeedWithCountsDto> feedWithCounts = queryFactory
        .select(
            Projections.constructor(FeedWithCountsDto.class,
                feed,
                likes.id.countDistinct(),
                comment.id.countDistinct()
            )
        )
        .from(feed)
        .join(feed.member, member).fetchJoin()
        .join(member.profile, profile).fetchJoin()
        .leftJoin(likes).on(likes.feed.eq(feed))
        .leftJoin(comment).on(comment.feed.eq(feed))
        .where(feed.id.in(ids))
        .groupBy(feed.id)
        .orderBy(FeedOrderSpecifier.getOrderBySpecifiers(condition.getSortBy()))
        .fetch();

    return feedWithCounts.stream()
        .map(FeedDto::from)
        .toList();
  }

  @Override
  public boolean supports(Class<? extends FeedQueryRequest> requestClass) {
    return MemberFeedSearchCondition.class.isAssignableFrom(requestClass);
  }
}