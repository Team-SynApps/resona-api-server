package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.LikeRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.Likes;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.exception.LikeException;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;
import synapps.resona.api.mysql.socialMedia.repository.LikesRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikesRepository likesRepository;
  private final FeedRepository feedRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  @Transactional
  public Likes register(LikeRequest request) {
    Feed feed = feedRepository.findById(request.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);
    MemberDto memberDto = memberService.getMember();
    Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

    Likes likes = Likes.of(member, feed);
    likesRepository.save(likes);
    return likes;
  }

  @Transactional
  public Likes cancel(Long likeId) {
    return likesRepository.findById(likeId).orElseThrow(LikeException::likeNotFound);
  }
}
