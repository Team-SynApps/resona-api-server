package com.synapps.resona.retrieval.service;

import static java.util.stream.Collectors.toMap;

import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.dto.FeedViewerContext;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.util.RedisKeyGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedTimelineService {
  private final RedisTemplate<String, String> redisTemplate;
  private final FeedReadRepository feedReadRepository;
  private final FeedQueryHelper feedQueryHelper;
  private final RedisTtlProperties redisTtlProperties;

  private static final int BATCH_MULTIPLIER = 5;
  private static final boolean filterSeenFeeds = false;

  /**
   * 개인화된 홈 피드(Home Feed)를 조회한다.
   *
   * <p>사용자가 팔로우하는 사람들의 피드로 구성된 개인화된 타임라인을 조회한다.
   * 카테고리 필터가 제공되면 해당 카테고리의 개인화 타임라인(예: timeline:123:TRAVEL)을,
   * 없으면 전체 개인화 타임라인(예: timeline:123:ALL)을 조회한다.
   * 실제 조회 로직은 중앙화된 {@code getFeedsFromTimeline} 메서드에 위임한다.</p>
   *
   * @param memberId 타임라인의 주인인 사용자 ID
   * @param targetLanguage 번역을 원하는 언어
   * @param cursor 페이지네이션을 위한 커서
   * @param size 한 페이지에 보여줄 피드 개수
   * @param category 필터링할 피드 카테고리 (nullable)
   * @return 커서 정보가 포함된 피드 DTO 목록
   */
  public CursorResult<FeedDto> getHomeFeeds(Long memberId, Language targetLanguage, String cursor, int size, FeedCategory category, FeedViewerContext viewerContext) {
    String timelineKey = (category != null) ? RedisKeyGenerator.getCategoryTimelineKey(memberId, category) : RedisKeyGenerator.getAllTimelineKey(memberId);
    return getFeedsFromTimeline(memberId, targetLanguage, cursor, size, timelineKey, category, viewerContext);
  }

  /**
   * 모든 사용자를 위한 공용 탐색 피드(Explore Feed)를 조회한다.
   *
   * <p>개인화된 타임라인과 달리, 이 피드는 팔로우 관계와 무관한 전체 사용자를 대상으로 한다.
   * 국가 및 카테고리 필터 조건에 맞는 미리 계산된 공용 타임라인(Redis)을 조회한다.
   * 조회된 결과에도 현재 로그인한 사용자의 개인화 필터(숨김/차단 목록)는 적용된다.</p>
   *
   * @param currentMemberId 현재 로그인하여 API를 호출한 사용자 ID (개인화 필터링용)
   * @param targetLanguage 번역을 원하는 언어
   * @param cursor 페이지네이션을 위한 커서
   * @param size 한 페이지에 보여줄 피드 개수
   * @param residence 필터링할 작성자의 거주 국가 (nullable)
   * @param category 필터링할 피드 카테고리 (nullable)
   * @return 커서 정보가 포함된 피드 DTO 목록
   */
  public CursorResult<FeedDto> getExploreFeeds(Long currentMemberId, Language targetLanguage, String cursor, int size, CountryCode residence, FeedCategory category, FeedViewerContext viewerContext) {
    String timelineKey = buildExploreTimelineKey(residence, category);
    return getFeedsFromTimeline(currentMemberId, targetLanguage, cursor, size, timelineKey, category, viewerContext);
  }

  /**
   * 개인화 타임라인이 비어있는 '콜드 스타트' 사용자를 위한 Fallback 피드를 조회한다.
   *
   * <p>이 메서드는 자체적으로 피드를 조회하는 로직을 수행하지 않고,
   * '탐색(Explore)' 피드 조회 로직을 재사용하여 '연결 다리' 역할을 한다.</p>
   *
   * @param memberId 현재 사용자 ID
   * @param targetLanguage 번역할 언어
   * @param size 페이지 크기
   * @param category 사용자가 원래 홈 피드에서 필터링하려 했던 카테고리 (null일 수 있음)
   * @return 탐색 피드 조회 결과
   */
  private CursorResult<FeedDto> getFallbackFeeds(Long memberId, Language targetLanguage, int size, String cursor, FeedCategory category, FeedViewerContext viewerContext) {
    log.info("Cold start detected for memberId: {}. Fetching fallback feeds for category: {}", memberId, category);
    return getExploreFeeds(memberId, targetLanguage, cursor, size, null, category, viewerContext);
  }

  @Async
  public void markFeedsAsSeen(Long memberId, List<Long> feedIds) {
    if (feedIds == null || feedIds.isEmpty()) {
      return;
    }
    String key = RedisKeyGenerator.getUserSeenFeedsKey(memberId);
    String[] feedIdStrings = feedIds.stream().map(String::valueOf).toArray(String[]::new);

    redisTemplate.opsForSet().add(key, feedIdStrings);
    redisTemplate.expire(key, redisTtlProperties.seenFeeds(), redisTtlProperties.unit());

    log.debug("Marked {} feeds as seen for memberId {}", feedIds.size(), memberId);
  }

  private CursorResult<FeedDto> getFeedsFromTimeline(Long memberId, Language targetLanguage, String cursor, int size, String timelineKey, FeedCategory category, FeedViewerContext viewerContext) {
    // 개인화 타임라인 콜드 스타트 처리
    Optional<CursorResult<FeedDto>> coldStartResult = handleColdStart(memberId, targetLanguage, size, cursor, timelineKey, category, viewerContext);
    if (coldStartResult.isPresent()) {
      return coldStartResult.get();
    }

    // Redis에서 후보 데이터 조회
    TimelineRequest request = TimelineRequest.from(cursor);
    LinkedHashSet<TypedTuple<String>> candidates = fetchCandidatesFromRedis(timelineKey, request.maxScore, size * BATCH_MULTIPLIER);

    // Redis 결과가 비었을 경우 Fallback 처리
    if (candidates.isEmpty()) {
      String fallbackKey = findNextFallbackKey(timelineKey);
      if (cursor == null && fallbackKey != null) {
        log.warn("Timeline '{}' is empty. Falling back to '{}'", timelineKey, fallbackKey);
        return getFeedsFromTimeline(memberId, targetLanguage, null, size, fallbackKey, category, viewerContext);
      }
      return new CursorResult<>(Collections.emptyList(), false, null);
    }

    // 커서 적용 및 1차 필터링 (읽음/숨김)
    List<TypedTuple<String>> preFilteredList = applyCursorAndFilters(candidates, request, memberId, size);
    if (preFilteredList.isEmpty()) {
      return new CursorResult<>(Collections.emptyList(), false, null);
    }

    // 다음 페이지 커서 계산
    CursorState cursorState = calculateNextCursor(preFilteredList, size);

    // DB 조회 및 2차 필터링 후 최종 결과 생성
    List<FeedDto> finalFeedDtos = finalizeFeeds(cursorState.pageContent(), memberId, targetLanguage, viewerContext);

    // 최종적으로 사용자에게 보여줄 피드 '봤음' 처리 -> 지금은 꺼둠
    if (filterSeenFeeds && !finalFeedDtos.isEmpty()) {
      markFeedsAsSeen(memberId, finalFeedDtos.stream().map(FeedDto::feedId).toList());
    }

    return new CursorResult<>(finalFeedDtos, cursorState.hasNext(), cursorState.nextCursor());
  }

  private record TimelineRequest(double maxScore, Long offsetId) {
    static TimelineRequest from(String cursor) {
      if (cursor == null || cursor.isEmpty()) {
        return new TimelineRequest(Double.POSITIVE_INFINITY, null);
      }
      String[] parts = cursor.split(":");
      if (parts.length == 2) {
        return new TimelineRequest(Double.parseDouble(parts[0]), Long.parseLong(parts[1]));
      }
      return new TimelineRequest(Double.POSITIVE_INFINITY, null);
    }
  }

  private record CursorState(List<TypedTuple<String>> pageContent, boolean hasNext, String nextCursor) {}

  /**
   * 개인화 타임라인의 콜드 스타트 상황을 확인하고, 필요시 Fallback 피드를 반환
   */
  private Optional<CursorResult<FeedDto>> handleColdStart(Long memberId, Language targetLanguage, int size, String cursor, String timelineKey, FeedCategory category, FeedViewerContext viewerContext) {
    if (RedisKeyGenerator.isPersonalTimelineKey(timelineKey, memberId)) {
      Long timelineSize = redisTemplate.opsForZSet().zCard(timelineKey);
      if (timelineSize == null || timelineSize == 0) {
        return Optional.of(getFallbackFeeds(memberId, targetLanguage, size, cursor, category, viewerContext));
      }
    }
    return Optional.empty();
  }

  /**
   * Redis ZSET에서 지정된 조건으로 피드 후보 목록을 조회
   */
  private LinkedHashSet<TypedTuple<String>> fetchCandidatesFromRedis(String timelineKey, double maxScore, int limit) {
    Set<TypedTuple<String>> tuples = redisTemplate.opsForZSet()
        .reverseRangeByScoreWithScores(timelineKey, Double.NEGATIVE_INFINITY, maxScore, 0, limit);
    return tuples != null ? new LinkedHashSet<>(tuples) : new LinkedHashSet<>();
  }

  /**
   * Redis에서 조회한 후보 목록에 커서 위치를 적용하고, 읽음/숨김 피드를 필터링
   */
  private List<TypedTuple<String>> applyCursorAndFilters(LinkedHashSet<TypedTuple<String>> candidates, TimelineRequest request, Long memberId, int size) {
    // 커서 위치 찾기
    List<TypedTuple<String>> candidatesAfterCursor = new ArrayList<>();
    if (request.offsetId != null) {
      boolean foundCursor = false;
      BigDecimal maxScoreDecimal = BigDecimal.valueOf(request.maxScore);

      for (TypedTuple<String> tuple : candidates) {
        if (foundCursor) {
          candidatesAfterCursor.add(tuple);
          continue;
        }
        BigDecimal tupleScoreDecimal = BigDecimal.valueOf(Objects.requireNonNull(tuple.getScore()));
        if (tupleScoreDecimal.compareTo(maxScoreDecimal) == 0 && Objects.equals(tuple.getValue(), String.valueOf(request.offsetId))) {
          foundCursor = true;
        }
      }
    } else {
      candidatesAfterCursor.addAll(candidates);
    }

    // 읽음/숨김 필터링
    Set<String> seenFeedIds = Collections.emptySet();
    if (filterSeenFeeds) {
      seenFeedIds = redisTemplate.opsForSet().members(RedisKeyGenerator.getUserSeenFeedsKey(memberId));
      seenFeedIds = (seenFeedIds == null) ? Collections.emptySet() : seenFeedIds;
    }

    Set<Long> hiddenFeedIds = feedQueryHelper.getHiddenFeedIds(memberId);

    final Set<String> finalSeenFeedIds = seenFeedIds;

    return candidatesAfterCursor.stream()
        .filter(tuple -> !finalSeenFeedIds.contains(tuple.getValue()))
        .filter(tuple -> !hiddenFeedIds.contains(Long.parseLong(Objects.requireNonNull(tuple.getValue()))))
        .limit(size + 1)
        .toList();
  }

  /**
   * 필터링된 목록을 기반으로 다음 페이지 유무와 커서 문자열을 계산
   */
  private CursorState calculateNextCursor(List<TypedTuple<String>> list, int size) {
    boolean hasNext = list.size() > size;
    String nextCursor = null;
    List<TypedTuple<String>> pageContent = new ArrayList<>(list);

    if (hasNext) {
      TypedTuple<String> nextCursorTuple = list.get(size);
      String scorePart = new BigDecimal(Objects.requireNonNull(nextCursorTuple.getScore()).toString()).toPlainString();
      nextCursor = scorePart + ":" + nextCursorTuple.getValue();
      pageContent.remove(size);
    }
    return new CursorState(pageContent, hasNext, nextCursor);
  }

  /**
   * 최종 피드 ID 목록으로 DB를 조회, 차단/본인 글 필터링, DTO 변환
   */
  private List<FeedDto> finalizeFeeds(List<TypedTuple<String>> finalCandidates, Long memberId, Language targetLanguage, FeedViewerContext viewerContext) {
    if (finalCandidates.isEmpty()) {
      return Collections.emptyList();
    }

    Set<Long> feedIdsToFetch = finalCandidates.stream()
        .map(tuple -> Long.parseLong(Objects.requireNonNull(tuple.getValue())))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    // DB 조회
    Map<Long, FeedDocument> feedDocumentMap = feedReadRepository.findAllByFeedIdIn(feedIdsToFetch).stream()
        .collect(toMap(FeedDocument::getFeedId, doc -> doc));

    // 2차 필터링 (차단, 본인 글) 및 DTO 변환
    Set<Long> blockedMemberIds = feedQueryHelper.getBlockedMemberIds(memberId);
    return feedIdsToFetch.stream()
        .map(feedDocumentMap::get)
        .filter(Objects::nonNull)
        .filter(doc -> !blockedMemberIds.contains(doc.getAuthor().getMemberId()))
        .filter(doc -> !doc.getAuthor().getMemberId().equals(memberId))
        .map(doc -> feedQueryHelper.translateAndConvertToDto(doc, targetLanguage, viewerContext))
        .collect(Collectors.toList());
  }

  private String findNextFallbackKey(String currentKey) {
    if (RedisKeyGenerator.isExploreCountryCategoryKey(currentKey)) {
      return currentKey.substring(0, currentKey.lastIndexOf(":"));
    }
    if (RedisKeyGenerator.isExploreCountryKey(currentKey) || RedisKeyGenerator.isExploreCategoryKey(currentKey)) {
      return RedisKeyGenerator.getExploreRecentKey();
    }
    return null;
  }

  private String buildExploreTimelineKey(CountryCode country, FeedCategory category) {
    if (country != null && category != null) {
      return RedisKeyGenerator.getExploreCountryCategoryKey(country.name(), category);
    } else if (country != null) {
      return RedisKeyGenerator.getExploreCountryKey(country.name());
    } else if (category != null) {
      return RedisKeyGenerator.getExploreCategoryKey(category);
    } else {
      return RedisKeyGenerator.getExploreRecentKey();
    }
  }
}