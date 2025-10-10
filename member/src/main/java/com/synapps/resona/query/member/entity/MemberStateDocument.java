package com.synapps.resona.query.member.entity;


import com.synapps.resona.entity.BaseDocument;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "member_state")
public class MemberStateDocument extends BaseDocument {

  @Id
  private Long memberId;

  private Set<Long> likedFeedIds;
  private Set<Long> scrappedFeedIds;
  private Set<Long> likedCommentIds;
  private Set<Long> likedReplyIds;

  private Set<Long> hiddenFeedIds;
  private Set<Long> hiddenCommentIds;
  private Set<Long> hiddenReplyIds;

   private Set<Long> blockedMemberIds;

  private MemberStateDocument(Long memberId) {
    this.memberId = memberId;
    this.likedFeedIds = new HashSet<>();
    this.scrappedFeedIds = new HashSet<>();
    this.likedCommentIds = new HashSet<>();
    this.likedReplyIds = new HashSet<>();
    this.hiddenFeedIds = new HashSet<>();
    this.hiddenCommentIds = new HashSet<>();
    this.hiddenReplyIds = new HashSet<>();
    this.blockedMemberIds = new HashSet<>();
  }

  public static MemberStateDocument create(Long memberId) {
    return new MemberStateDocument(memberId);
  }

  public boolean isFeedLiked(Long feedId) {
    return likedFeedIds.contains(feedId);
  }

  public boolean isScrapped(Long feedId) {
    return scrappedFeedIds.contains(feedId);
  }
}
