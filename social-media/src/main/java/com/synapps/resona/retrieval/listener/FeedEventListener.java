package com.synapps.resona.retrieval.listener;

import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.query.member.service.MemberStateService;
import com.synapps.resona.retrieval.service.FeedDocumentUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedEventListener {

  private final MemberStateService memberStateService;
  private final FeedDocumentUpdateService feedDocumentUpdateService;

  @Async
  @EventListener
  public void handleFeedLikeChanged(FeedLikeChangedEvent event) {
    log.info("Feed like changed event received for memberId: {}, feedId: {}, delta: {}",
        event.memberId(), event.feedId(), event.delta());

    // 개인 상태 업데이트 (내가 좋아요 눌렀는지 여부)
    if (event.delta() > 0) {
      memberStateService.addLikedFeed(event.memberId(), event.feedId());
    } else {
      memberStateService.removeLikedFeed(event.memberId(), event.feedId());
    }

    // 공통 데이터 업데이트 (피드의 총 좋아요 수)
    feedDocumentUpdateService.updateFeedLikeCount(event.feedId(), event.delta());
  }
}