package synapps.resona.api.mysql.socialMedia.service.comment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentResponse;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyResponse;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.repository.comment.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.feed.FeedRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final FeedRepository feedRepository;


  @Transactional
  public CommentResponse register(CommentRequest request) {
    Feed feed = feedRepository.findWithMemberById(request.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);
    Comment comment = Comment.of(feed, feed.getMember(), request.getContent());
    commentRepository.save(comment);
    return CommentResponse.from(comment);
  }

  @Transactional
  public CommentResponse edit(CommentUpdateRequest request) {
    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);
    comment.updateContent(request.getContent());
    return CommentResponse.from(comment);
  }

  public CommentResponse getComment(long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    return CommentResponse.from(comment);
  }

  @Transactional
  public List<CommentResponse> getCommentsByFeedId(long viewerId, long feedId) {
    List<Comment> comments = commentRepository.findAllCommentsByFeedIdWithReplies(viewerId, feedId);

    return comments.stream().map(CommentResponse::from).toList();
  }

  @Transactional
  public List<ReplyResponse> getReplies(long commentId) {
    Comment comment = commentRepository.findWithReplies(commentId)
        .orElseThrow(CommentException::commentNotFound);
    // TODO: 예외처리 해야 함.
    return comment.getReplies().stream()
        .map(ReplyResponse::from)
        .toList();
  }

  @Transactional
  public Comment deleteComment(long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    comment.softDelete();
    return comment;
  }
}
