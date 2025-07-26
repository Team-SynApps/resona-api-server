package synapps.resona.api.mysql.socialMedia.service.mention;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.mention.MentionResponse; // 추가
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.MentionException;
import synapps.resona.api.mysql.socialMedia.repository.comment.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.mention.MentionRepository;

@Service
@RequiredArgsConstructor
public class MentionService {

  private final MentionRepository mentionRepository;
  private final MemberService memberService;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public MentionResponse register(Long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    String email = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(email).orElseThrow();

    Mention mention = Mention.of(member, comment, LocalDateTime.now());
    mentionRepository.save(mention);

    return MentionResponse.from(mention);
  }

  public MentionResponse read(Long mentionId) {
    Mention mention = mentionRepository.findById(mentionId).orElseThrow(MentionException::mentionNotFound);
    return MentionResponse.from(mention);
  }

  @Transactional
  public void delete(Long mentionId) {
    Mention mention = mentionRepository.findById(mentionId)
        .orElseThrow(MentionException::mentionNotFound);
    mention.softDelete();
  }
}