package com.synapps.resona.retrieval.service;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import com.synapps.resona.common.event.FeedTranslationUpdatedEvent;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.dto.FeedViewerContext;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.entity.FeedDocument.LocationEmbed;
import com.synapps.resona.retrieval.query.entity.FeedDocument.MediaEmbed;
import com.synapps.resona.translation.service.TranslationService;
import com.synapps.resona.util.RedisKeyGenerator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedQueryHelper {
  private final RedisTemplate<String, String> redisTemplate;
  private final MemberStateService memberStateService;
  private final TranslationService translationService;
  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Cache-Aside -> 좋아요한 피드 ID 목록 조회
   */
  public Set<Long> getLikedFeedIds(Long memberId) {
    String key = RedisKeyGenerator.getMemberLikedFeedKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getLikedFeedIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getLikedFeedIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }

    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 스크랩한 피드 ID 목록 조회
   */
  public Set<Long> getScrappedFeedIds(Long memberId) {
    String key = RedisKeyGenerator.getMemberScrappedFeedKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getScrappedFeedIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getScrappedFeedIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }

    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 좋아요한 댓글 ID 목록 조회
   */
  public Set<Long> getLikedCommentIds(Long memberId) {
    String key = RedisKeyGenerator.getLikedCommentKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getLikedCommentIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getLikedCommentIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }

    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 좋아요한 대댓글 ID 목록 조회
   */
  public Set<Long> getLikedReplyIds(Long memberId) {
    String key = RedisKeyGenerator.getLikedReplyKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getLikedReplyIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getLikedReplyIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }

    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 숨김 처리된 피드 ID 목록 조회
   */
  public Set<Long> getHiddenFeedIds(Long memberId) {
    String key = RedisKeyGenerator.getHiddenFeedsKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getHiddenFeedIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getHiddenFeedIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }
    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 숨김 처리된 댓글 ID 목록 조회
   */
  public Set<Long> getHiddenCommentIds(Long memberId) {
    String key = RedisKeyGenerator.getHiddenCommentsKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getHiddenCommentIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getHiddenCommentIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }
    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 숨김 처리된 대댓글 ID 목록 조회
   */
  public Set<Long> getHiddenReplyIds(Long memberId) {
    String key = RedisKeyGenerator.getHiddenRepliesKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getHiddenReplyIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getHiddenReplyIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }
    return Collections.emptySet();
  }

  /**
   * Cache-Aside -> 차단한 사용자 ID 목록 조회
   */
  public Set<Long> getBlockedMemberIds(Long memberId) {
    String key = RedisKeyGenerator.getBlockedUsersKey(memberId);
    Set<String> cachedIds = redisTemplate.opsForSet().members(key);

    if (cachedIds != null && !cachedIds.isEmpty()) {
      return cachedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(memberId);
    if (memberState != null && !memberState.getBlockedMemberIds().isEmpty()) {
      Set<Long> idsFromMongo = memberState.getBlockedMemberIds();
      redisTemplate.opsForSet().add(key, idsFromMongo.stream().map(String::valueOf).toArray(String[]::new));
      return idsFromMongo;
    }
    return Collections.emptySet();
  }

  public FeedDto translateAndConvertToDto(FeedDocument doc, Language targetLanguage, FeedViewerContext context) {
    String translatedContent = doc.getTranslations().stream()
        .filter(t -> t.languageCode().equals(targetLanguage.getCode()))
        .findFirst()
        .map(Translation::content)
        .orElseGet(() -> {
          if (doc.getLanguage().equals(targetLanguage)) {
            return doc.getContent();
          }
          String translatedContext = translationService.translateForRealTime(doc.getContent(), doc.getLanguage(), targetLanguage);
          applicationEventPublisher.publishEvent(new FeedTranslationUpdatedEvent(doc.getFeedId(), translatedContext, targetLanguage));
          return translatedContext;
        });

    return toDto(doc, translatedContent, context);
  }

  public FeedDto toDto(FeedDocument doc, String translatedContent, FeedViewerContext context) {
    Author authorDto = Author.of(
        doc.getAuthor().getMemberId(),
        doc.getAuthor().getNickname(),
        doc.getAuthor().getProfileImageUrl(),
        doc.getAuthor().getCountryOfResidence()
    );

    List<MediaEmbed> mediaDtos = doc.getMedias().stream()
        .map(m -> MediaEmbed.of(m.getMediaType(), m.getUrl(), m.getIndex()))
        .collect(Collectors.toList());

    LocationEmbed locationDto = doc.getLocation() != null ? LocationEmbed.of(
        doc.getLocation().getPlaceId(),
        doc.getLocation().getDisplayName(),
        doc.getLocation().getFormattedAddress(),
        doc.getLocation().getLocation(),
        doc.getLocation().getCategory(),
        doc.getLocation().getPrimaryType()
    ) : null;

    return new FeedDto(
        doc.getFeedId(),
        authorDto,
        doc.getCategory(),
        doc.getContent(),
        mediaDtos,
        locationDto,
        doc.getLanguage().getCode(),
        doc.getLikeCount(),
        doc.getCommentCount(),
        translatedContent,
        context.likedFeedIds().contains(doc.getFeedId()),
        context.scrappedFeedIds().contains(doc.getFeedId()),
        doc.getCreatedAt(),
        doc.getModifiedAt()
    );
  }
}
