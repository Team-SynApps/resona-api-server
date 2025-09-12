package synapps.resona.api.socialMedia.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.ReplyProjectionDto;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyRequest;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyUpdateRequest;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.comment.entity.ContentDisplayStatus;
import synapps.resona.api.socialMedia.comment.entity.Reply;
import synapps.resona.api.socialMedia.comment.exception.CommentException;
import synapps.resona.api.socialMedia.comment.exception.ReplyException;
import synapps.resona.api.socialMedia.comment.repository.comment.CommentRepository;
import synapps.resona.api.socialMedia.comment.repository.reply.ReplyQueryRepository;
import synapps.resona.api.socialMedia.comment.repository.reply.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {

  private final ReplyQueryRepository replyQueryRepository;
  private final ReplyProcessor replyProcessor;
  private final ReplyRepository replyRepository;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public ReplyDto register(ReplyRequest request, MemberDto memberDto) {
    Member member = memberRepository.findById(memberDto.getId()).orElseThrow();
    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);
    comment.addReply();
    Reply reply = Reply.of(comment, member, request.getContent());
    replyRepository.save(reply);

    return ReplyDto.of(reply, ContentDisplayStatus.NORMAL, reply.getContent());
  }

  @Transactional
  public ReplyDto update(ReplyUpdateRequest request) {
    Reply reply = replyRepository.findWithCommentAndMemberById(request.getReplyId())
        .orElseThrow(ReplyException::replyNotFound);
    reply.update(request.getContent());

    return ReplyDto.of(reply, ContentDisplayStatus.NORMAL, reply.getContent());
  }

  @Transactional(readOnly = true)
  public List<ReplyDto> readAll(Long viewerId, Long commentId) {
    List<ReplyProjectionDto> projections = replyQueryRepository.findAllRepliesByCommentId(viewerId, commentId);

    return replyProcessor.process(projections);
  }

  @Transactional
  public void delete(Long replyId) {
    Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);
    Comment comment = reply.getComment();
    comment.removeReply();
    reply.softDelete();
  }
}