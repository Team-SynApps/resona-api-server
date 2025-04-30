package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.entity.Mention;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.MentionException;
import synapps.resona.api.mysql.socialMedia.repository.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.MentionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentionService {
    private final MentionRepository mentionRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Mention register(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Mention mention = Mention.of(member, comment, LocalDateTime.now());
        mentionRepository.save(mention);

        return mention;
    }

    public Mention read(Long mentionId) {
        return mentionRepository.findById(mentionId).orElseThrow(MentionException::mentionNotFound);
    }

    @Transactional
    public Mention delete(Long mentionId) {
        Mention mention = mentionRepository.findById(mentionId).orElseThrow(MentionException::mentionNotFound);
        mention.softDelete();

        return mention;
    }
}
