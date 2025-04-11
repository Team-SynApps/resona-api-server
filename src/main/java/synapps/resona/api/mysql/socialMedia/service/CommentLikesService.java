package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.comment.request.CommentLikesRequest;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.mysql.socialMedia.exception.CommentException;
import synapps.resona.api.mysql.socialMedia.exception.LikeException;
import synapps.resona.api.mysql.socialMedia.repository.CommentLikesRepository;
import synapps.resona.api.mysql.socialMedia.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentLikesService {
    private final CommentLikesRepository commentLikesRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentLikes register(CommentLikesRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(CommentException::commentNotFound);

        String email = memberService.getMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow();

        CommentLikes commentLikes = CommentLikes.of(member, comment);
        commentLikesRepository.save(commentLikes);
        return commentLikes;
    }

    @Transactional
    public CommentLikes cancel(Long commentLikeId) {
        return commentLikesRepository.findById(commentLikeId)
                .orElseThrow(LikeException::likeNotFound);
    }
}
