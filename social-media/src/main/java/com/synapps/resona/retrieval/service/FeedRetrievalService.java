package com.synapps.resona.retrieval.service;

import com.synapps.resona.common.dto.CommentDto;
import com.synapps.resona.comment.query.service.CommentRetrievalService;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.retrieval.dto.FeedDetailDto;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.dto.FeedViewerContext;
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
  private final FeedViewerContextFactory feedViewerContextFactory;

  private final FeedQueryHelper feedQueryHelper;
  private final CommentRetrievalService commentRetrievalService;

  public CursorResult<FeedDto> getHomeFeeds(Long viewerId, Language targetLanguage, String cursor, int size, FeedCategory category) {
    FeedViewerContext viewerContext = feedViewerContextFactory.create(viewerId);
    return feedTimelineService.getHomeFeeds(viewerId, targetLanguage, cursor, size, category, viewerContext);
  }

  public CursorResult<FeedDto> getExploreFeeds(Long viewerId, Language targetLanguage, String cursor, int size, CountryCode residence, FeedCategory category) {
    FeedViewerContext viewerContext = feedViewerContextFactory.create(viewerId);
    return feedTimelineService.getExploreFeeds(viewerId, targetLanguage, cursor, size, residence, category, viewerContext);
  }

  public Page<FeedDto> getMyFeeds(Long viewerId, Language targetLanguage, Pageable pageable) {
    FeedViewerContext viewerContext = feedViewerContextFactory.create(viewerId);
    return memberFeedService.getMyFeeds(viewerId, targetLanguage, pageable, viewerContext);
  }

  public Page<FeedDto> getMyScrappedFeeds(Long viewerId, Language targetLanguage, Pageable pageable) {
    FeedViewerContext viewerContext = feedViewerContextFactory.create(viewerId);
    return memberFeedService.getMyScrappedFeeds(viewerId, targetLanguage, pageable, viewerContext);
  }

  public FeedDetailDto getFeedDetail(Long feedId, Long viewerId, Language targetLanguage, Pageable pageable) {
    FeedViewerContext viewerContext = feedViewerContextFactory.create(viewerId);

    FeedDocument feedDocument = feedReadRepository.findByFeedId(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedDto baseFeedDto = feedQueryHelper.translateAndConvertToDto(feedDocument, targetLanguage, viewerContext);
    Page<CommentDto> comments = commentRetrievalService.getCommentsForFeed(feedId, viewerId, targetLanguage, pageable);

    return FeedDetailDto.of(baseFeedDto, comments, feedDocument.getCreatedAt(), feedDocument.getModifiedAt());
  }
}