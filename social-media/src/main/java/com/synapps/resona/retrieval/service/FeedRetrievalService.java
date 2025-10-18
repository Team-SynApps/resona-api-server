package com.synapps.resona.retrieval.service;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.query.service.CommentRetrievalService;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDetailDto;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedRetrievalService {

  private final FeedTimelineService feedTimelineService;
  private final MemberFeedService memberFeedService;
  private final FeedReadRepository feedReadRepository;

  private final FeedQueryHelper feedQueryHelper;
  private final MemberStateService memberStateService;
  private final CommentRetrievalService commentRetrievalService;

  public CursorResult<FeedDto> getHomeFeeds(Long memberId, Language targetLanguage, String cursor, int size, FeedCategory category) {
    return feedTimelineService.getHomeFeeds(memberId, targetLanguage, cursor, size, category);
  }

  public CursorResult<FeedDto> getExploreFeeds(Long currentMemberId, Language targetLanguage, String cursor, int size, CountryCode residence, FeedCategory category) {
    return feedTimelineService.getExploreFeeds(currentMemberId, targetLanguage, cursor, size, residence, category);
  }

  public Page<FeedDto> getMyFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    return memberFeedService.getMyFeeds(memberId, targetLanguage, pageable);
  }

  public Page<FeedDto> getMyScrappedFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    return memberFeedService.getMyScrappedFeeds(memberId, targetLanguage, pageable);
  }

  public FeedDetailDto getFeedDetail(Long feedId, Long currentMemberId, Language targetLanguage, Pageable pageable) {
    FeedDocument feedDocument = feedReadRepository.findByFeedId(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedDto baseFeedDto = feedQueryHelper.translateAndConvertToDto(feedDocument, targetLanguage);
    Page<CommentDto> comments = commentRetrievalService.getCommentsForFeed(feedId, currentMemberId, targetLanguage, pageable);

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(currentMemberId);

    return FeedDetailDto.of(baseFeedDto, comments, memberState, feedDocument.getCreatedAt(), feedDocument.getModifiedAt());
  }
}