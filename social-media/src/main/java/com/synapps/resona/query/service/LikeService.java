package com.synapps.resona.query.service;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.exception.CommentException;
import com.synapps.resona.exception.ReplyException;
import com.synapps.resona.domain.repository.comment.comment.CommentRepository;
import com.synapps.resona.domain.repository.comment.reply.ReplyRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.exception.FeedException;
import com.synapps.resona.domain.repository.feed.FeedRepository;
import com.synapps.resona.query.dto.likes.response.CommentLikesResponse;
import com.synapps.resona.query.dto.likes.response.FeedLikesResponse;
import com.synapps.resona.query.dto.likes.response.ReplyLikesResponse;
import com.synapps.resona.domain.entity.likes.CommentLikes;
import com.synapps.resona.domain.entity.likes.FeedLikes;
import com.synapps.resona.domain.entity.likes.ReplyLikes;
import com.synapps.resona.exception.LikeException;
import com.synapps.resona.domain.repository.likes.CommentLikesRepository;
import com.synapps.resona.domain.repository.likes.FeedLikesRepository;
import com.synapps.resona.domain.repository.likes.ReplyLikesRepository;
import com.synapps.resona.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final FeedLikesRepository feedLikeRepository;
  private final CommentLikesRepository commentLikeRepository;
  private final ReplyLikesRepository replyLikeRepository;

  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public FeedLikesResponse likeFeed(Long feedId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);

    if (feedLikeRepository.existsByFeedAndMember(feed, member)) {
      throw LikeException.alreadyLiked();
    }

    FeedLikes feedLike = feedLikeRepository.save(FeedLikes.of(member, feed));

    long feedLikesCount = feedLikeRepository.countByFeedId(feedId);
    return FeedLikesResponse.of(feedLike.getFeed().getId(), feedLikesCount, !feedLike.isDeleted());
  }

  @Transactional
  public FeedLikesResponse unlikeFeed(Long feedId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);

    FeedLikes feedLike = feedLikeRepository.findByFeedAndMember(feed, member)
        .orElseThrow(LikeException::likeNotFound);

    feedLike.softDelete();
    long feedLikesCount = feedLikeRepository.countByFeedId(feedId);
    return FeedLikesResponse.of(feedId, feedLikesCount, feedLike.isDeleted());
  }

  @Transactional
  public CommentLikesResponse likeComment(Long commentId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);

    if (commentLikeRepository.existsByCommentAndMember(comment, member)) {
      throw LikeException.alreadyLiked();
    }

    CommentLikes commentLike = commentLikeRepository.save(CommentLikes.of(member, comment));
    long commentLikesCount = commentLikeRepository.countByCommentId(commentId);
    return CommentLikesResponse.of(commentLike.getComment().getId(), commentLikesCount, !commentLike.isDeleted());
  }

  @Transactional
  public CommentLikesResponse unlikeComment(Long commentId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);

    CommentLikes commentLike = commentLikeRepository.findByCommentAndMember(comment, member)
        .orElseThrow(LikeException::likeNotFound);

    commentLike.softDelete();
    long commentLikesCount = commentLikeRepository.countByCommentId(commentId);
    return CommentLikesResponse.of(commentId, commentLikesCount, commentLike.isDeleted());
  }

  @Transactional
  public ReplyLikesResponse likeReply(Long replyId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);

    if (replyLikeRepository.existsByReplyAndMember(reply, member)) {
      throw LikeException.alreadyLiked();
    }

    ReplyLikes replyLike = replyLikeRepository.save(ReplyLikes.of(member, reply));
    long replyLikesCount = replyLikeRepository.countByReplyId(replyId);
    return ReplyLikesResponse.of(replyLike.getReply().getId(), replyLikesCount, !replyLike.isDeleted());
  }

  @Transactional
  public ReplyLikesResponse unlikeReply(Long replyId, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);

    ReplyLikes replyLike = replyLikeRepository.findByReplyAndMember(reply, member)
        .orElseThrow(LikeException::likeNotFound);

    replyLike.softDelete();
    long replyLikesCount = replyLikeRepository.countByReplyId(replyId);
    return ReplyLikesResponse.of(replyId, replyLikesCount, replyLike.isDeleted());
  }
}