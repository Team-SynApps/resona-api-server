package com.synapps.resona.comment.service;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.CommentProjectionDto;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.dto.request.CommentRequest;
import com.synapps.resona.comment.dto.request.CommentUpdateRequest;
import com.synapps.resona.comment.dto.response.CommentDeleteResponse;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.comment.repository.comment.CommentQueryRepository;
import com.synapps.resona.comment.repository.comment.CommentRepository;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.feed.repository.dsl.FeedExpressions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentQueryRepository commentQueryRepository;
  private final CommentProcessor commentProcessor;
  private final FeedRepository feedRepository;
  private final ReplyService replyService;

  private final FeedExpressions feedExpressions;

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
  public CommentDeleteResponse deleteComment(long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    comment.softDelete();
    long commentCount = feedExpressions.fetchCommentCountByCommentId(commentId);
    return CommentDeleteResponse.of(commentCount);
  }
}