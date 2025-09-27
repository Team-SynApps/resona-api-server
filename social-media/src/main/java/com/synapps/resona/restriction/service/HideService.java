package com.synapps.resona.restriction.service;

import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.entity.Reply;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.comment.repository.comment.CommentRepository;
import com.synapps.resona.comment.repository.reply.ReplyRepository;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.restriction.entity.CommentHide;
import com.synapps.resona.restriction.entity.FeedHide;
import com.synapps.resona.restriction.entity.ReplyHide;
import com.synapps.resona.restriction.repository.HideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HideService {

  private final HideRepository hideRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

  @Transactional
  public void hideFeed(Long feedId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedHide feedHide = FeedHide.of(member, feed);
    hideRepository.save(feedHide);
  }

  @Transactional
  public void hideComment(Long commentId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);

    CommentHide commentHide = CommentHide.of(member, comment);
    hideRepository.save(commentHide);
  }

  @Transactional
  public void hideReply(Long replyId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Reply reply = replyRepository.findById(replyId)
        .orElseThrow(ReplyException::replyNotFound);

    ReplyHide replyHide = ReplyHide.of(member, reply);
    hideRepository.save(replyHide);
  }
}
