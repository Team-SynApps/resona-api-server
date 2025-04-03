package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.LikeRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Likes;
import synapps.resona.api.mysql.social_media.exception.FeedException;
import synapps.resona.api.mysql.social_media.exception.LikeNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.LikeRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public Likes register(LikeRequest request){
        Feed feed = feedRepository.findById(request.getFeedId()).orElseThrow(FeedException::feedNotFoundException);
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Likes likes = Likes.of(member, feed);
        likeRepository.save(likes);
        return likes;
    }

    @Transactional
    public Likes cancel(Long likeId) throws LikeNotFoundException {
        return likeRepository.findById(likeId).orElseThrow(LikeNotFoundException::new);
    }
}
