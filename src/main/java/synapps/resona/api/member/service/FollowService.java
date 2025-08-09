package synapps.resona.api.member.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberProfileDto;
import synapps.resona.api.member.entity.member.Follow;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.FollowException;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.FollowRepository;
import synapps.resona.api.member.repository.member.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;

  public void follow(Long toMemberId) {
    String email = memberService.getMemberEmail();
    Member fromMember = memberRepository.findByEmail(email)
        .orElseThrow(MemberException::memberNotFound);

    if (fromMember.getId().equals(toMemberId)) {
      throw FollowException.cantFollowMyself();
    }

    Member toMember = memberRepository.findById(toMemberId)
        .orElseThrow(MemberException::followingNotFound);

    boolean alreadyFollow = followRepository.existsByFollowerAndFollowing(fromMember, toMember);

    if (alreadyFollow) {
      throw FollowException.alreadyFollowing();
    }

    Follow follow = Follow.of(fromMember, toMember);
    followRepository.save(follow);
  }

  public void unfollow(Long toMemberId) {
    String email = memberService.getMemberEmail();
    Member fromMember = memberRepository.findByEmail(email)
        .orElseThrow(MemberException::memberNotFound);
    Member toMember = memberRepository.findById(toMemberId)
        .orElseThrow(MemberException::followingNotFound);

    Follow follow = followRepository.findByFollowerAndFollowing(fromMember, toMember)
        .orElseThrow(FollowException::relationshipNotFound);

    followRepository.delete(follow);
  }

  public List<MemberProfileDto> getFollowers(Long memberId) {
    return followRepository.findFollowersByFollowingId(memberId)
        .stream()
        .map(follow -> MemberProfileDto.from(follow.getFollower(),
            follow.getFollower().getProfile()))
        .collect(Collectors.toList());
  }

  public List<MemberProfileDto> getFollowings(Long memberId) {
    return followRepository.findFollowingsByFollowerId(memberId)
        .stream()
        .map(follow -> MemberProfileDto.from(follow.getFollowing(),
            follow.getFollowing().getProfile()))
        .collect(Collectors.toList());
  }
}
