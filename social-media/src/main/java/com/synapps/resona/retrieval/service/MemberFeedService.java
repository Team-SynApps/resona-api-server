package com.synapps.resona.retrieval.service;

import com.synapps.resona.entity.Language;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.translation.service.TranslationService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFeedService {
  private final FeedReadRepository feedReadRepository;
  private final MemberStateService memberStateService;
  private final TranslationService translationService;
  private final FeedQueryHelper feedQueryHelper;

  public Page<FeedDto> getMyFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    Set<Long> hiddenFeedIds = feedQueryHelper.getHiddenFeedIds(memberId);
    Page<FeedDocument> feedPage = feedReadRepository.findByAuthor_MemberIdOrderByCreatedAtDesc(memberId, pageable);

    List<FeedDto> feedDtos = feedPage.getContent().stream()
        .filter(doc -> !hiddenFeedIds.contains(doc.getFeedId()))
        .map(doc -> feedQueryHelper.translateAndConvertToDto(doc, targetLanguage))
        .collect(Collectors.toList());

    return new PageImpl<>(feedDtos, pageable, feedPage.getTotalElements());
  }

  public Page<FeedDto> getMyScrappedFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    // MemberStateService를 통해 스크랩한 모든 피드 ID를 가져옴 (캐시 우선)
    Set<Long> scrappedFeedIds = memberStateService.getScrappedFeedIds(memberId);

    if (scrappedFeedIds.isEmpty()) {
      return Page.empty(pageable);
    }

    // 해당 ID 목록으로 MongoDB에서 FeedDocument 조회
    Page<FeedDocument> feedPage = feedReadRepository.findByFeedIdInOrderByCreatedAtDesc(scrappedFeedIds, pageable);


    List<FeedDto> feedDtos = feedPage.getContent().stream()
        .map(doc -> feedQueryHelper.translateAndConvertToDto(doc, targetLanguage))
        .collect(Collectors.toList());

    return new PageImpl<>(feedDtos, pageable, feedPage.getTotalElements());
  }
}
