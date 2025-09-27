package com.synapps.resona.likes.entity;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.entity.Feed;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("FEED")
public class FeedLikes extends Likes {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  private FeedLikes(Member member, Feed feed) {
    this.setMember(member);
    this.feed = feed;
  }

  public static FeedLikes of(Member member, Feed feed) {
    return new FeedLikes(member, feed);
  }
}