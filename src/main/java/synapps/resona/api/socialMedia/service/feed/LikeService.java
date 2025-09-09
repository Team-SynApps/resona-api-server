package synapps.resona.api.socialMedia.service.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.socialMedia.dto.like.request.LikeRequest;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.feed.Likes;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.exception.LikeException;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.repository.feed.LikesRepository;
import synapps.resona.api.socialMedia.dto.like.response.LikeResponse;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikesRepository likesRepository;
  private final FeedRepository feedRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  @Transactional
  public LikeResponse register(Long feedId, Long memberId) {
    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(FeedException::feedNotFoundException);
    Member member = memberRepository.findById(memberId).orElseThrow();

    Likes likes = Likes.of(member, feed);
    return LikeResponse.from(likesRepository.save(likes));
  }

  @Transactional
  public void cancel(Long feedId, Long memberId) {
    Likes like = likesRepository.findLikesByFeedId(feedId, memberId).orElseThrow(LikeException::likeNotFound);
    like.softDelete();
  }
}
