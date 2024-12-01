package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.LikeRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Like;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.exception.LikeNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.LikeRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;

    @Transactional
    public Like register(LikeRequest request) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(request.getFeedId()).orElseThrow(FeedNotFoundException::new);
        Member member = memberService.getMember();

        Like like = Like.of(member, feed, LocalDateTime.now());
        likeRepository.save(like);
        return like;
    }

    @Transactional
    public Like cancel(Long likeId) throws LikeNotFoundException {
        Like like = likeRepository.findById(likeId).orElseThrow(LikeNotFoundException::new);
        like.cancelLike();
        return like;
    }
}
