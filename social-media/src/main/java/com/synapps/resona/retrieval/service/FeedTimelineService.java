package com.synapps.resona.retrieval.service;

import static java.util.stream.Collectors.toMap;

import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
  public CursorResult<FeedDto> getHomeFeeds(Long memberId, Language targetLanguage, String cursor, int size, FeedCategory category) {
    String categorySuffix = (category != null) ? ":" + category.name() : ":ALL";
    String timelineKey = "timeline:" + memberId + categorySuffix;

    return getFeedsFromTimeline(memberId, targetLanguage, cursor, size, timelineKey, category);
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
  public CursorResult<FeedDto> getExploreFeeds(Long currentMemberId, Language targetLanguage, String cursor, int size, CountryCode residence, FeedCategory category) {
    String timelineKey = buildExploreTimelineKey(residence, category);
    return getFeedsFromTimeline(currentMemberId, targetLanguage, cursor, size, timelineKey, category);
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
  private CursorResult<FeedDto> getFallbackFeeds(Long memberId, Language targetLanguage, int size, FeedCategory category) {
    log.info("Cold start detected for memberId: {}. Fetching fallback feeds for category: {}", memberId, category);
    return getExploreFeeds(memberId, targetLanguage, null, size, null, category);
  }

  @Async
  public void markFeedsAsSeen(Long memberId, List<Long> feedIds) {
    if (feedIds == null || feedIds.isEmpty()) {
      return;
    }
    String key = "user:" + memberId + ":seen_feeds";
    String[] feedIdStrings = feedIds.stream().map(String::valueOf).toArray(String[]::new);

    redisTemplate.opsForSet().add(key, feedIdStrings);
    redisTemplate.expire(key, redisTtlProperties.seenFeeds(), redisTtlProperties.unit());

    log.debug("Marked {} feeds as seen for memberId {}", feedIds.size(), memberId);
  }

  private CursorResult<FeedDto> getFeedsFromTimeline(Long memberId, Language targetLanguage, String cursor, int size, String timelineKey, FeedCategory category) {
    // 개인화 타임라인 콜드 스타트
    Long timelineSize = redisTemplate.opsForZSet().zCard(timelineKey);
    if (timelineKey.startsWith("timeline:" + memberId) && cursor == null && (timelineSize == null || timelineSize == 0)) {
      return getFallbackFeeds(memberId, targetLanguage, size, category);
    }

    final int BATCH_MULTIPLIER = 5; // 요청한 size의 5배수만큼 후보를 가져옴 -> 필터링되어 오기 때문
    final int candidateSize = size * BATCH_MULTIPLIER;

    double maxScore = (cursor != null) ? Double.parseDouble(cursor) : Double.POSITIVE_INFINITY;

    // Redis에서 데이터 조회
    Set<TypedTuple<String>> candidateTuples = redisTemplate.opsForZSet()
        .reverseRangeByScoreWithScores(timelineKey, Double.NEGATIVE_INFINITY, maxScore, 0, candidateSize);

    // 공용 타임라인 폴백 로직
    if ((candidateTuples == null || candidateTuples.isEmpty())) {
      // 첫 페이지 요청이고, 조회하려던 것이 '공용 타임라인'이었을 경우
      if (cursor == null && !timelineKey.startsWith("timeline:" + memberId)) {

        // 더 넓은 범위의 타임라인을 찾아서 대신 조회
        String fallbackKey = findNextFallbackKey(timelineKey);
        if (fallbackKey != null) {
          log.warn("Timeline '{}' is empty. Falling back to '{}'", timelineKey, fallbackKey);
          return getFeedsFromTimeline(memberId, targetLanguage, cursor, size, fallbackKey, category);
        }
      }
      // fallback 할 곳이 없거나, 개인 타임라인이 비어있는 경우는 그냥 빈 결과 반환
      return new CursorResult<>(Collections.emptyList(), false, null);
    }

    // 필터링에 필요한 ID 목록을 미리 준비
    String seenFeedsKey = "user:" + memberId + ":seen_feeds";
    Set<String> seenFeedIds = redisTemplate.opsForSet().members(seenFeedsKey);
    if (seenFeedIds == null) {
      seenFeedIds = Collections.emptySet(); // NPE 방지
    }
    Set<Long> hiddenFeedIds = feedQueryHelper.getHiddenFeedIds(memberId);

    // 후보군에서 '본 피드'와 '숨김 피드'를 한번에 필터링하고, size+1 만큼만 남김
    Set<String> finalSeenFeedIds = seenFeedIds;
    List<TypedTuple<String>> preFilteredList = candidateTuples.stream()
        .filter(tuple -> !finalSeenFeedIds.contains(tuple.getValue()))
        .filter(tuple -> !hiddenFeedIds.contains(Long.parseLong(Objects.requireNonNull(tuple.getValue()))))
        .limit(size + 1)
        .toList();

    if (preFilteredList.isEmpty()) {
      return new CursorResult<>(Collections.emptyList(), false, null);
    }

    // 필터링된 결과를 기준으로 다음 페이지 유무와 커서를 계산
    List<TypedTuple<String>> finalListForCursor = new ArrayList<>(preFilteredList);
    boolean hasNext = false;
    String nextCursor = null;

    if (finalListForCursor.size() > size) {
      hasNext = true;
      nextCursor = String.valueOf(Objects.requireNonNull(finalListForCursor.get(size).getScore()).longValue());
      finalListForCursor.remove(size); // 최종 결과 목록에서는 size+1 번째 아이템 제거
    }

    Set<Long> feedIdsToFetch = finalListForCursor.stream()
        .map(tuple -> Long.parseLong(Objects.requireNonNull(tuple.getValue())))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (feedIdsToFetch.isEmpty()) {
      return new CursorResult<>(Collections.emptyList(), false, null);
    }

    // DB 조회 및 2차 필터링(차단, 본인 글) 수행
    Map<Long, FeedDocument> feedDocumentMap = feedReadRepository.findAllByFeedIdIn(feedIdsToFetch).stream()
        .collect(toMap(FeedDocument::getFeedId, doc -> doc));
    Set<Long> blockedMemberIds = feedQueryHelper.getBlockedMemberIds(memberId);

    List<FeedDto> finalFeedDtos = feedIdsToFetch.stream()
        .map(feedDocumentMap::get)
        .filter(Objects::nonNull)
        .filter(doc -> !blockedMemberIds.contains(doc.getAuthor().getMemberId()))
        .filter(doc -> !doc.getAuthor().getMemberId().equals(memberId))
        .map(doc -> feedQueryHelper.translateAndConvertToDto(doc, targetLanguage))
        .collect(Collectors.toList());

    // 최종적으로 사용자에게 보내는 피드 ID 목록만 '봤음' 처리
    if (!finalFeedDtos.isEmpty()) {
      List<Long> seenIds = finalFeedDtos.stream().map(FeedDto::feedId).toList();
      markFeedsAsSeen(memberId, seenIds);
    }

    return new CursorResult<>(finalFeedDtos, hasNext, nextCursor);
  }

  private String findNextFallbackKey(String currentKey) {
    if (currentKey.matches("timeline:country:\\w+:\\w+")) {
      return currentKey.substring(0, currentKey.lastIndexOf(":"));
    }
    if (currentKey.startsWith("timeline:country:") || currentKey.startsWith("timeline:category:")) {
      return "feeds:recent";
    }
    return null;
  }

  private String buildExploreTimelineKey(CountryCode country, FeedCategory category) {
    if (country != null && category != null) {
      return "timeline:country:" + country.name() + ":" + category.name();
    } else if (country != null) {
      return "timeline:country:" + country.name();
    } else if (category != null) {
      return "timeline:category:" + category.name();
    } else {
      return "feeds:recent";
    }
  }

}
