package com.synapps.resona.security;

import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.ScrapRepository;
import com.synapps.resona.comment.command.repository.MentionRepository;
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
  private final ReplyRepository replyRepository;

  public boolean isFeedMemberProperty(Long feedId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return feedRepository.existsByIdAndMember(feedId, member);
  }

  public boolean isCommentMemberProperty(Long commentId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return commentRepository.existsByIdAndMember(commentId, member);
  }

  public boolean isReplyMemberProperty(Long replyId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return replyRepository.existsByIdAndMember(replyId, member);
  }
}
