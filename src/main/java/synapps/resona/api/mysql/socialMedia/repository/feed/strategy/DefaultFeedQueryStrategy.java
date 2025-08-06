package synapps.resona.api.mysql.socialMedia.repository.feed.strategy;

import static synapps.resona.api.mysql.member.entity.member.QMember.member;
import static synapps.resona.api.mysql.member.entity.profile.QProfile.profile;
import static synapps.resona.api.mysql.socialMedia.entity.comment.QReply.reply;
import static synapps.resona.api.mysql.socialMedia.entity.feed.QFeed.feed;
import static synapps.resona.api.mysql.socialMedia.entity.feed.QLikes.likes;
import static synapps.resona.api.mysql.socialMedia.entity.comment.QComment.comment;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QBlock.block;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QFeedHide.feedHide;
import static synapps.resona.api.mysql.socialMedia.entity.restriction.QHide.hide;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.dto.feed.DefaultFeedSearchCondition;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedWithCountsDto;
import synapps.resona.api.mysql.socialMedia.repository.feed.dsl.FeedOrderSpecifier;
import synapps.resona.api.mysql.socialMedia.repository.feed.dsl.FeedPredicate;

@Repository
@RequiredArgsConstructor
public class DefaultFeedQueryStrategy implements FeedQueryStrategy<DefaultFeedSearchCondition> {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<FeedDto> findFeeds(DefaultFeedSearchCondition condition, LocalDateTime cursor, Pageable pageable) {
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
    List<FeedWithCountsDto> feedWithCounts = queryFactory
        .select(
            Projections.constructor(FeedWithCountsDto.class,
                feed,
                likes.id.countDistinct(),   // 좋아요 수
                comment.id.countDistinct()  // 댓글 수
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
    return DefaultFeedSearchCondition.class.isAssignableFrom(requestClass);
  }
}