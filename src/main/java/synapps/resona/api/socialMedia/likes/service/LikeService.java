package synapps.resona.api.socialMedia.likes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.likes.dto.response.CommentLikesResponse;
import synapps.resona.api.socialMedia.likes.dto.response.FeedLikesResponse;
import synapps.resona.api.socialMedia.likes.dto.response.ReplyLikesResponse;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.comment.entity.Reply;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.likes.entity.CommentLikes;
import synapps.resona.api.socialMedia.likes.entity.FeedLikes;
import synapps.resona.api.socialMedia.likes.entity.ReplyLikes;
import synapps.resona.api.socialMedia.comment.exception.CommentException;
import synapps.resona.api.socialMedia.feed.exception.FeedException;
import synapps.resona.api.socialMedia.likes.exception.LikeException;
import synapps.resona.api.socialMedia.comment.exception.ReplyException;
import synapps.resona.api.socialMedia.comment.repository.comment.CommentRepository;
import synapps.resona.api.socialMedia.comment.repository.reply.ReplyRepository;
import synapps.resona.api.socialMedia.feed.repository.FeedRepository;
import synapps.resona.api.socialMedia.likes.repository.CommentLikesRepository;
import synapps.resona.api.socialMedia.likes.repository.FeedLikesRepository;
import synapps.resona.api.socialMedia.likes.repository.ReplyLikesRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    return FeedLikesResponse.of(feedId, feedLikesCount - 1, feedLike.isDeleted());
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
    return CommentLikesResponse.of(commentId, commentLikesCount - 1, commentLike.isDeleted());
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
    return ReplyLikesResponse.of(replyId, replyLikesCount -1, replyLike.isDeleted());
  }
}