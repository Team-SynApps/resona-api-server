package com.synapps.resona.command.service;

import com.synapps.resona.command.dto.response.MemberProfileDto;
import com.synapps.resona.command.entity.member.Follow;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.event.FollowChangedEvent;
import com.synapps.resona.exception.FollowException;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.command.repository.member.FollowRepository;
import com.synapps.resona.command.repository.member.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

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

    eventPublisher.publishEvent(new FollowChangedEvent(fromMember.getId(), toMember.getId(), FollowChangedEvent.FollowAction.FOLLOW));
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

    eventPublisher.publishEvent(new FollowChangedEvent(fromMember.getId(), toMember.getId(), FollowChangedEvent.FollowAction.UNFOLLOW));
  }

}
