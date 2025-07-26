package synapps.resona.api.mysql.socialMedia.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.dto.comment.response.CommentLikeResponse; // DTO 임포트
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.LikeException;
import synapps.resona.api.mysql.socialMedia.repository.comment.CommentLikesRepository;
import synapps.resona.api.mysql.socialMedia.repository.comment.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentLikesService {

  private final CommentLikesRepository commentLikesRepository;
  private final CommentRepository commentRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  @Transactional
  public CommentLikeResponse register(CommentLikesRequest request) {
    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);

    String email = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(email).orElseThrow();

    CommentLikes commentLikes = CommentLikes.of(member, comment);
    commentLikesRepository.save(commentLikes);
    return CommentLikeResponse.from(commentLikes); // DTO로 변환하여 반환
  }

  @Transactional
  public void cancel(Long commentLikeId) {
    CommentLikes commentLike = commentLikesRepository.findById(commentLikeId)
        .orElseThrow(LikeException::likeNotFound);
    commentLikesRepository.delete(commentLike);
  }
}