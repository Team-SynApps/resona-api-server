package com.synapps.resona.feed.repository.dsl;


import static com.synapps.resona.feed.entity.QFeed.feed;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;

public final class FeedPredicate {

  private FeedPredicate() {}

  /**
   * memberId가 일치하는 조건 생성
   * @param memberId 검색할 회원 ID
   * @return memberId가 null이 아니면 BooleanExpression을, null이면 null을 반환
   */
  public static BooleanExpression eqMemberId(Long memberId) {
    if (memberId == null) {
      return null;
    }
    return feed.member.id.eq(memberId);
  }

  /**
   * 커서(cursor) 시간보다 이전에 생성된 피드를 찾는 조건 생성
   * @param cursor 기준 시간
   * @return cursor가 null이 아니면 BooleanExpression을, null이면 null을 반환
   */
  public static BooleanExpression createdAtBefore(LocalDateTime cursor) {
    if (cursor == null) {
      return null;
    }
    return feed.createdAt.before(cursor);
  }
}