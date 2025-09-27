package com.synapps.resona.security;

import com.synapps.resona.domain.repository.comment.comment.CommentRepository;
import com.synapps.resona.domain.repository.comment.reply.ReplyRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.repository.feed.FeedRepository;
import com.synapps.resona.domain.repository.feed.ScrapRepository;
import com.synapps.resona.domain.repository.comment.MentionRepository;
import com.synapps.resona.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component("socialSecurity")
@RequiredArgsConstructor
public class SocialSecurity {

  private static final Logger logger = LoggerFactory.getLogger(SocialSecurity.class);
  private final MemberService memberService;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final MentionRepository mentionRepository;
  private final ScrapRepository scrapRepository;
  private final ReplyRepository replyRepository;

  public boolean isFeedMemberProperty(Long feedId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return feedRepository.existsByIdAndMember(feedId, member);
  }

  public boolean isCommentMemberProperty(Long commentId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return commentRepository.existsByIdAndMember(commentId, member);
  }

  public boolean isScrapMemberProperty(Long scrapId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return scrapRepository.existsByIdAndMember(scrapId, member);
  }

  public boolean isMentionMemberProperty(Long mentionId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return mentionRepository.existsByIdAndMember(mentionId, member);
  }

  public boolean isReplyMemberProperty(Long replyId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return replyRepository.existsByIdAndMember(replyId, member);
  }
}
