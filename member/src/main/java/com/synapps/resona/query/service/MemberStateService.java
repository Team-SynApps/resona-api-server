package com.synapps.resona.query.service;

import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.repository.MemberStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class MemberStateService {

  private final MemberStateRepository memberStateRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final MongoTemplate mongoTemplate;

  public MemberStateDocument getMemberStateDocument(Long memberId) {
    return memberStateRepository.findById(memberId).orElse(null);
  }

  // --- Feed State ---
  public void addLikedFeed(Long memberId, Long feedId) { updateState(memberId, "likedFeedIds", "liked_feeds", feedId, true); }
  public void removeLikedFeed(Long memberId, Long feedId) { updateState(memberId, "likedFeedIds", "liked_feeds", feedId, false); }
  public void addScrappedFeed(Long memberId, Long feedId) { updateState(memberId, "scrappedFeedIds", "scrapped_feeds", feedId, true); }
  public void removeScrappedFeed(Long memberId, Long feedId) { updateState(memberId, "scrappedFeedIds", "scrapped_feeds", feedId, false); }
  public void addHiddenFeed(Long memberId, Long feedId) { updateState(memberId, "hiddenFeedIds", "hidden_feeds", feedId, true); }
  public void removeHiddenFeed(Long memberId, Long feedId) { updateState(memberId, "hiddenFeedIds", "hidden_feeds", feedId, false); }

  // --- Comment State ---
  public void addLikedComment(Long memberId, Long commentId) { updateState(memberId, "likedCommentIds", "liked_comments", commentId, true); }
  public void removeLikedComment(Long memberId, Long commentId) { updateState(memberId, "likedCommentIds", "liked_comments", commentId, false); }
  public void addHiddenComment(Long memberId, Long commentId) { updateState(memberId, "hiddenCommentIds", "hidden_comments", commentId, true); }
  public void removeHiddenComment(Long memberId, Long commentId) { updateState(memberId, "hiddenCommentIds", "hidden_comments", commentId, false); }

  // --- Reply State ---
  public void addLikedReply(Long memberId, Long replyId) { updateState(memberId, "likedReplyIds", "liked_replies", replyId, true); }
  public void removeLikedReply(Long memberId, Long replyId) { updateState(memberId, "likedReplyIds", "liked_replies", replyId, false); }
  public void addHiddenReply(Long memberId, Long replyId) { updateState(memberId, "hiddenReplyIds", "hidden_replies", replyId, true); }
  public void removeHiddenReply(Long memberId, Long replyId) { updateState(memberId, "hiddenReplyIds", "hidden_replies", replyId, false); }

  // --- Block State ---
  public void addBlockedMember(Long blockerId, Long blockedId) { updateState(blockerId, "blockedMemberIds", "blocked_users", blockedId, true); }
  public void removeBlockedMember(Long blockerId, Long unblockedId) { updateState(blockerId, "blockedMemberIds", "blocked_users", unblockedId, false); }

  // --- 읽기(Read) 메서드들 (Cache-Aside 패턴) ---
  public Set<Long> getLikedFeedIds(Long memberId) { return getIdsFromState(memberId, "liked_feeds", MemberStateDocument::getLikedFeedIds); }
  public Set<Long> getScrappedFeedIds(Long memberId) { return getIdsFromState(memberId, "scrapped_feeds", MemberStateDocument::getScrappedFeedIds); }
  public Set<Long> getHiddenFeedIds(Long memberId) { return getIdsFromState(memberId, "hidden_feeds", MemberStateDocument::getHiddenFeedIds); }
  public Set<Long> getBlockedMemberIds(Long memberId) { return getIdsFromState(memberId, "blocked_users", MemberStateDocument::getBlockedMemberIds); }
  public Set<Long> getLikedCommentIds(Long memberId) { return getIdsFromState(memberId, "liked_comments", MemberStateDocument::getLikedCommentIds); }
  public Set<Long> getHiddenCommentIds(Long memberId) { return getIdsFromState(memberId, "hidden_comments", MemberStateDocument::getHiddenCommentIds); }
  public Set<Long> getLikedReplyIds(Long memberId) { return getIdsFromState(memberId, "liked_replies", MemberStateDocument::getLikedReplyIds); }
  public Set<Long> getHiddenReplyIds(Long memberId) { return getIdsFromState(memberId, "hidden_replies", MemberStateDocument::getHiddenReplyIds); }

  // --- Private Helper Methods ---

  private void updateState(Long memberId, String fieldName, String redisKeySuffix, Long targetId, boolean isAdding) {
    // MongoDB에 atomic 연산으로 업데이트
    Query query = new Query(where("_id").is(memberId));
    Update update = isAdding
        ? new Update().addToSet(fieldName, targetId)
        : new Update().pull(fieldName, targetId);
    mongoTemplate.upsert(query, update, MemberStateDocument.class);

    // Redis 캐시 업데이트
    String key = "user:" + memberId + ":" + redisKeySuffix;
    if (isAdding) {
      redisTemplate.opsForSet().add(key, String.valueOf(targetId));
    } else {
      redisTemplate.opsForSet().remove(key, String.valueOf(targetId));
    }
  }

  private Set<Long> getIdsFromState(Long memberId, String keySuffix, Function<MemberStateDocument, Set<Long>> mongoFetcher) {
    String key = "user:" + memberId + ":" + keySuffix;
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateRepository.findById(memberId).orElse(null);
    if (memberState != null) {
      Set<Long> idsFromMongo = mongoFetcher.apply(memberState);
      if (idsFromMongo != null && !idsFromMongo.isEmpty()) {
        redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
        return idsFromMongo;
      }
    }
    return Collections.emptySet();
  }
}