package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Mention;
import synapps.resona.api.mysql.social_media.exception.CommentNotFoundException;
import synapps.resona.api.mysql.social_media.exception.MentionNotFoundException;
import synapps.resona.api.mysql.social_media.repository.CommentRepository;
import synapps.resona.api.mysql.social_media.repository.MentionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentionService {
    private final MentionRepository mentionRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Mention register(Long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Mention mention = Mention.of(member, comment, LocalDateTime.now());
        mentionRepository.save(mention);

        return mention;
    }

    public Mention read(Long mentionId) throws MentionNotFoundException {
        return mentionRepository.findById(mentionId).orElseThrow(MentionNotFoundException::new);
    }

    @Transactional
    public Mention delete(Long mentionId) throws MentionNotFoundException {
        Mention mention = mentionRepository.findById(mentionId).orElseThrow(MentionNotFoundException::new);
        mention.softDelete();

        return mention;
    }
}
