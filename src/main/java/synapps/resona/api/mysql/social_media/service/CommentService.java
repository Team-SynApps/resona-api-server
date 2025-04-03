package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentPostResponse;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentReadResponse;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentUpdateResponse;
import synapps.resona.api.mysql.social_media.dto.reply.response.ReplyReadResponse;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Reply;
import synapps.resona.api.mysql.social_media.exception.CommentException;
import synapps.resona.api.mysql.social_media.exception.FeedException;
import synapps.resona.api.mysql.social_media.repository.CommentRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.ReplyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;


    @Transactional
    public CommentPostResponse register(CommentRequest request) {
        Feed feed = feedRepository.findWithMemberById(request.getFeedId()).orElseThrow(FeedException::feedNotFoundException);
        Comment comment = Comment.of(feed, feed.getMember(), request.getContent());
        commentRepository.save(comment);
        return new CommentPostResponse(comment);
    }

    @Transactional
    public CommentUpdateResponse edit(CommentUpdateRequest request){
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentException::commentNotFound);
        comment.updateContent(request.getContent());
        return new CommentUpdateResponse(comment);
    }

    public CommentReadResponse getComment(long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);
        return new CommentReadResponse(comment);
    }

    @Transactional
    public List<CommentPostResponse> getCommentsByFeedId(long feedId){
        List<Comment> comments = commentRepository.findAllCommentsByFeedId(feedId);

        return comments.stream().map(CommentPostResponse::new).toList();
    }

    @Transactional
    public List<ReplyReadResponse> getReplies(long commentId){
        Comment comment = commentRepository.findWithReplies(commentId).orElseThrow(CommentException::commentNotFound);
        // TODO: 예외처리 해야 함.
        return comment.getReplies().stream()
                .map((reply)-> new ReplyReadResponse(reply, commentId))
                .toList();
    }

    @Transactional
    public Comment deleteComment(long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);
        comment.softDelete();
        return comment;
    }
}
