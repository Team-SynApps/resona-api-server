package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyPostResponse;
import synapps.resona.api.mysql.socialMedia.dto.reply.response.ReplyReadResponse;
import synapps.resona.api.mysql.socialMedia.entity.Comment;
import synapps.resona.api.mysql.socialMedia.entity.Reply;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.ReplyException;
import synapps.resona.api.mysql.socialMedia.repository.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final MemberService memberService;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReplyPostResponse register(ReplyRequest request){
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow(CommentException::commentNotFound);
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
    public ReplyReadResponse update(ReplyUpdateRequest request) {
        Reply reply = replyRepository.findWithCommentById(request.getReplyId()).orElseThrow(ReplyException::replyNotFound);
        reply.update(request.getContent());
        return new ReplyReadResponse(reply, reply.getComment().getId());
    }


    public ReplyReadResponse read(Long replyId) {
        Reply reply = replyRepository.findWithCommentById(replyId).orElseThrow(ReplyException::replyNotFound);
        return new ReplyReadResponse(reply, reply.getComment().getId());
    }

    @Transactional
    public Reply delete(Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);
        Comment comment = reply.getComment();
        comment.removeReply();
        reply.softDelete();

        return reply;
    }
}
