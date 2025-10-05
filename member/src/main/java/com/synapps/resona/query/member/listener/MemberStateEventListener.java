package com.synapps.resona.query.member.listener;


import com.synapps.resona.query.member.event.FeedHiddenEvent;
import com.synapps.resona.query.member.event.MemberBlockedEvent;
import com.synapps.resona.query.member.event.MemberUnblockedEvent;
import com.synapps.resona.query.member.service.MemberStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStateEventListener {

  private final MemberStateService memberStateService;

  @Async
  @EventListener
  public void handleFeedHiddenEvent(FeedHiddenEvent event) {
    log.info("Received FeedHiddenEvent for memberId: {}, feedId: {}", event.memberId(), event.feedId());
    try {
      memberStateService.addHiddenFeed(event.memberId(), event.feedId());
    } catch (Exception e) {
      log.error("Failed to handle FeedHiddenEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleMemberBlockedEvent(MemberBlockedEvent event) {
    log.info("Received MemberBlockedEvent for blockerId: {}, blockedId: {}", event.blockerId(), event.blockedId());
    try {
      memberStateService.addBlockedMember(event.blockerId(), event.blockedId());
    } catch (Exception e) {
      log.error("Failed to handle MemberBlockedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleMemberUnblockedEvent(MemberUnblockedEvent event) {
    log.info("Received MemberUnblockedEvent for blockerId: {}, unblockedId: {}", event.blockerId(), event.unblockedId());
    try {
      memberStateService.removeBlockedMember(event.blockerId(), event.unblockedId());
    } catch (Exception e) {
      log.error("Failed to handle MemberUnblockedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleCommentLikedEvent(com.synapps.resona.query.member.event.CommentLikedEvent event) {
    log.info("Received CommentLikedEvent for memberId: {}, commentId: {}", event.memberId(), event.commentId());
    try {
      memberStateService.addLikedComment(event.memberId(), event.commentId());
    } catch (Exception e) {
      log.error("Failed to handle CommentLikedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleCommentUnlikedEvent(com.synapps.resona.query.member.event.CommentUnlikedEvent event) {
    log.info("Received CommentUnlikedEvent for memberId: {}, commentId: {}", event.memberId(), event.commentId());
    try {
      memberStateService.removeLikedComment(event.memberId(), event.commentId());
    } catch (Exception e) {
      log.error("Failed to handle CommentUnlikedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleReplyLikedEvent(com.synapps.resona.query.member.event.ReplyLikedEvent event) {
    log.info("Received ReplyLikedEvent for memberId: {}, replyId: {}", event.memberId(), event.replyId());
    try {
      memberStateService.addLikedReply(event.memberId(), event.replyId());
    } catch (Exception e) {
      log.error("Failed to handle ReplyLikedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }

  @Async
  @EventListener
  public void handleReplyUnlikedEvent(com.synapps.resona.query.member.event.ReplyUnlikedEvent event) {
    log.info("Received ReplyUnlikedEvent for memberId: {}, replyId: {}", event.memberId(), event.replyId());
    try {
      memberStateService.removeLikedReply(event.memberId(), event.replyId());
    } catch (Exception e) {
      log.error("Failed to handle ReplyUnlikedEvent", e);
      // TODO: 이벤트 처리 실패 시 재시도 또는 에러 로깅 정책 구현
    }
  }
}