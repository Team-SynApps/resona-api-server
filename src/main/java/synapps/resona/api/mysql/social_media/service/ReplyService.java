package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.social_media.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.reply.response.ReplyPostResponse;
import synapps.resona.api.mysql.social_media.dto.reply.response.ReplyReadResponse;
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
    private final MemberRepository memberRepository;

    @Transactional
    public ReplyPostResponse register(ReplyRequest request) throws CommentNotFoundException {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentNotFoundException::new);
        comment.addReply();
        Reply reply = Reply.of(comment, member, request.getContent());
        replyRepository.save(reply);
        return ReplyPostResponse.builder()
                .commentId(comment.getId().toString())
                .replyId(reply.getId().toString())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt().toString())
                .build();
    }

    @Transactional
    public ReplyReadResponse update(ReplyUpdateRequest request) throws ReplyNotFoundException {
        Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow(ReplyNotFoundException::new);
        reply.update(request.getContent());
        return ReplyReadResponse.builder()
                .replyId(reply.getId().toString())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt().toString())
                .build();
    }


    public ReplyReadResponse read(Long replyId) throws ReplyNotFoundException {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        return ReplyReadResponse.builder()
                .replyId(reply.getId().toString())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt().toString())
                .build();
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
