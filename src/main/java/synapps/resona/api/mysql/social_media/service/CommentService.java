package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.CommentRequest;
import synapps.resona.api.mysql.social_media.dto.CommentUpdateRequest;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.CommentRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;

    @Transactional
    public Comment register(CommentRequest request) throws FeedNotFoundException {
        Member member = memberService.getMember();
        Feed feed = feedRepository.findById(request.getFeedId()).orElseThrow(FeedNotFoundException::new);
        Comment comment = Comment.of(feed, member, request.getContent(), LocalDateTime.now(), LocalDateTime.now());
        commentRepository.save(comment);
        return comment;
    }

    @Transactional
    public Comment edit(CommentUpdateRequest request) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentNotFoundException::new);
        comment.updateContent(request.getContent());
        return comment;
    }

    public Comment getComment(long commentId) throws CommentNotFoundException {
        return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public Comment deleteComment(long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.softDelete();
        return comment;
    }
}
