package synapps.resona.api.socialMedia.service.comment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.socialMedia.dto.reply.request.ReplyRequest;
import synapps.resona.api.socialMedia.dto.reply.request.ReplyUpdateRequest;
import synapps.resona.api.socialMedia.dto.reply.response.ReplyResponse;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.comment.Reply;
import synapps.resona.api.socialMedia.exception.CommentException;
import synapps.resona.api.socialMedia.exception.ReplyException;
import synapps.resona.api.socialMedia.repository.comment.CommentRepository;
import synapps.resona.api.socialMedia.repository.comment.reply.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {

  private final MemberService memberService;
  private final ReplyRepository replyRepository;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public ReplyResponse register(ReplyRequest request, MemberDto memberDto) {
    Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);
    comment.addReply();
    Reply reply = Reply.of(comment, member, request.getContent());
    replyRepository.save(reply);

    return ReplyResponse.from(reply, comment.getId());
  }

  @Transactional
  public ReplyResponse update(ReplyUpdateRequest request) {
    Reply reply = replyRepository.findWithCommentById(request.getReplyId())
        .orElseThrow(ReplyException::replyNotFound);
    reply.update(request.getContent());
    return ReplyResponse.from(reply);
  }

  @Transactional
  public List<ReplyResponse> readAll(Long viewerId, Long commentId) {
    List<Reply> replies = replyRepository.findAllRepliesByCommentId(viewerId, commentId);

    return replies.stream().map(ReplyResponse::from).toList();
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
