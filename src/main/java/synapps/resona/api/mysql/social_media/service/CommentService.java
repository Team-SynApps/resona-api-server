package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentRequest;
import synapps.resona.api.mysql.social_media.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentPostResponse;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentReadResponse;
import synapps.resona.api.mysql.social_media.dto.comment.response.CommentUpdateResponse;
import synapps.resona.api.mysql.social_media.dto.reply.response.ReplyReadResponse;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Reply;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.CommentRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.ReplyRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ReplyRepository replyRepository;


    @Transactional
    public CommentPostResponse register(CommentRequest request) throws FeedNotFoundException {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Feed feed = feedRepository.findById(request.getFeedId()).orElseThrow(FeedNotFoundException::new);
        Comment comment = Comment.of(feed, member, request.getContent(), LocalDateTime.now(), LocalDateTime.now());
        commentRepository.save(comment);
        return CommentPostResponse.builder()
                .commentId(comment.getId().toString())
                .content(comment.getContent())
                .build();
    }

    @Transactional
    public CommentUpdateResponse edit(CommentUpdateRequest request) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentNotFoundException::new);
        comment.updateContent(request.getContent());
        return CommentUpdateResponse.builder()
                .commentId(comment.getId().toString())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .modifiedAt(comment.getModifiedAt().toString())
                .build();
    }

    public CommentReadResponse getComment(long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        return CommentReadResponse.builder()
                .commentId(comment.getId().toString())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }

    @Transactional
    public List<CommentPostResponse> getCommentsByFeedId(long feedId) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
        List<Comment> comments = commentRepository.findAllByFeed(feed);
        List<CommentPostResponse> commentResponse = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponse.add(CommentPostResponse.builder()
                    .content(comment.getContent())
                    .commentId(comment.getId().toString())
                    .createdAt(comment.getCreatedAt().toString())
                    .build());
        }
        return commentResponse;
    }

    @Transactional
    public List<ReplyReadResponse> getReplies(long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        List<Reply> replies = replyRepository.findAllByComment(comment);
        // TODO: 예외처리 해야 함.
        List<ReplyReadResponse> repliesResponse = new ArrayList<>();
        for (Reply reply : replies) {
            repliesResponse.add(ReplyReadResponse.builder()
                    .replyId(reply.getId().toString())
                    .commentId(comment.getId().toString())
                    .content(reply.getContent())
                    .createdAt(reply.getCreatedAt().toString()).build());
        }
        return repliesResponse;
    }

    @Transactional
    public Comment deleteComment(long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.softDelete();
        return comment;
    }
}
