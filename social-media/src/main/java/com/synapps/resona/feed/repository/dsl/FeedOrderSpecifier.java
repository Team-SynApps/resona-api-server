package com.synapps.resona.feed.repository.dsl;

import static com.synapps.resona.feed.entity.QFeed.feed;
import static com.synapps.resona.likes.entity.QFeedLikes.feedLikes;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.synapps.resona.feed.dto.FeedSortBy;


public final class FeedOrderSpecifier {

  private FeedOrderSpecifier() {}

  public static OrderSpecifier<?>[] getOrderBySpecifiers(FeedSortBy sortBy) {
    if (sortBy == null) {
      // 기본값은 최신순
      return new OrderSpecifier[]{ feed.createdAt.desc() };
    }

    return switch (sortBy) {
      case POPULAR -> new OrderSpecifier[]{
          feedLikes.id.countDistinct().desc(),
          feed.createdAt.desc()
      };
      case RANDOM -> new OrderSpecifier[]{
          Expressions.numberTemplate(Double.class, "function('RAND')").asc()
      };
      default -> // LATEST
          new OrderSpecifier[]{
              feed.createdAt.desc()
          };
    };
  }
}