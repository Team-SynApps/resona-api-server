package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.ReplyRequest;
import synapps.resona.api.mysql.social_media.dto.ReplyUpdateRequest;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Reply;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.ReplyNotFoundException;
import synapps.resona.api.mysql.social_media.repository.CommentRepository;
import synapps.resona.api.mysql.social_media.repository.ReplyRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final MemberService memberService;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Reply register(ReplyRequest request) throws CommentNotFoundException {
        Member member = memberService.getMember();
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentNotFoundException::new);
        comment.addReply();
        Reply reply = Reply.of(comment, member, request.getContent(), LocalDateTime.now(), LocalDateTime.now());
        replyRepository.save(reply);
        return reply;
    }

    @Transactional
    public Reply update(ReplyUpdateRequest request) throws ReplyNotFoundException {
        Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow(ReplyNotFoundException::new);
        reply.update(request.getContent());
        return reply;
    }


    public Reply read(Long replyId) throws ReplyNotFoundException {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        return reply;
    }

    @Transactional
    public Reply delete(Long replyId) throws ReplyNotFoundException {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        Comment comment = reply.getComment();
        comment.removeReply();
        reply.softDelete();

        return reply;
    }
}
