package synapps.resona.api.socialMedia.service.comment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.socialMedia.dto.comment.CommentDto;
import synapps.resona.api.socialMedia.dto.comment.CommentProjectionDto;
import synapps.resona.api.socialMedia.dto.comment.CommentWithStatusDto;
import synapps.resona.api.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.socialMedia.dto.comment.ReplyDto;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.exception.CommentException;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.repository.comment.comment.CommentQueryRepository;
import synapps.resona.api.socialMedia.repository.comment.comment.CommentRepository;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentQueryRepository commentQueryRepository;
  private final CommentProcessor commentProcessor;
  private final FeedRepository feedRepository;
  private final ReplyService replyService;

  @Transactional
  public CommentDto register(CommentRequest request) {
    Feed feed = feedRepository.findWithMemberById(request.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);
    Comment comment = Comment.of(feed, feed.getMember(), request.getContent());
    commentRepository.save(comment);

    return CommentDto.of(comment);
  }

  @Transactional
  public CommentDto edit(CommentUpdateRequest request) {
    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);
    comment.updateContent(request.getContent());
    return CommentDto.of(comment);
  }

  public CommentDto getComment(long viewerId, long commentId) {
    CommentProjectionDto projectionDto = commentQueryRepository.findCommentProjectionById(viewerId, commentId)
        .orElseThrow(CommentException::commentNotFound);

    return commentProcessor.processSingle(projectionDto);
  }

  public List<CommentDto> getCommentsByFeedId(long viewerId, long feedId) {
    List<CommentProjectionDto> comments = commentQueryRepository.findAllCommentsByFeedIdWithReplies(viewerId, feedId);
    return commentProcessor.process(comments);
  }

  public List<ReplyDto> getReplies(long viewerId, long commentId) {

    if (!commentRepository.existsById(commentId)) {
      throw CommentException.commentNotFound();
    }

    return replyService.readAll(viewerId, commentId);
  }

  @Transactional
  public void deleteComment(long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    comment.softDelete();
  }
}