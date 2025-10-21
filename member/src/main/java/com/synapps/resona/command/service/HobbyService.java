package com.synapps.resona.command.service;

import com.synapps.resona.command.entity.hobby.GivenHobby;
import com.synapps.resona.command.entity.hobby.Hobby;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.command.event.HobbyAddedEvent;
import com.synapps.resona.command.event.HobbyNameUpdatedEvent;
import com.synapps.resona.command.event.HobbyRemovedEvent;
import com.synapps.resona.command.repository.hobby.HobbyRepository;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.exception.HobbyException;
import com.synapps.resona.exception.MemberException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HobbyService {

  private final HobbyRepository hobbyRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public Hobby registerCustomHobby(String name) {
    Member member = memberService.getMemberUsingSecurityContext();
    MemberDetails memberDetails = member.getMemberDetails();
    Hobby hobby = Hobby.of(memberDetails, name);
    hobbyRepository.save(hobby);
    eventPublisher.publishEvent(new HobbyAddedEvent(member.getId(), name));
    return hobby;
  }

  @Transactional
  public Set<Hobby> registerHobbies(Set<String> hobbies) {
    Member member = memberService.getMemberUsingSecurityContext();
    MemberDetails memberDetails = member.getMemberDetails();
    Set<Hobby> createdHobbies = new HashSet<>();
    hobbies.forEach(hobbyName -> {
          GivenHobby givenHobby = GivenHobby.of(hobbyName);
          Hobby hobby = givenHobby.equals(GivenHobby.NOT_GIVEN)
              ? Hobby.of(memberDetails, hobbyName)
              : Hobby.of(memberDetails, givenHobby);
          Hobby createdHobby = hobbyRepository.save(hobby);
          createdHobbies.add(createdHobby);
          eventPublisher.publishEvent(new HobbyAddedEvent(member.getId(), hobby.getName()));
        });
    return createdHobbies;
  }

  @Transactional
  public Set<Hobby> syncHobbies(Set<String> newHobbyNames) {
    Member member = memberService.getMemberUsingSecurityContext();
    MemberDetails memberDetails = member.getMemberDetails();

    List<Hobby> existingHobbies = hobbyRepository.findAllByMemberDetailsIdWithSoftDeleted(memberDetails.getId());

    Map<String, Hobby> existingHobbyMap = existingHobbies.stream()
        .collect(Collectors.toMap(Hobby::getName, hobby -> hobby));

    Set<Hobby> hobbies = new HashSet<>();

    for (String newName : newHobbyNames) {
      Hobby hobby = existingHobbyMap.get(newName);

      if (hobby == null) {
        Hobby newHobby = Hobby.of(memberDetails, newName);
        hobbyRepository.save(newHobby);
        hobbies.add(newHobby);
        eventPublisher.publishEvent(new HobbyAddedEvent(member.getId(), newName));
      } else if (hobby.isDeleted()) {
        hobby.restore();
        hobbies.add(hobby);
        eventPublisher.publishEvent(new HobbyAddedEvent(member.getId(), newName));
      }
    }

    for (Hobby existingHobby : existingHobbies) {
      if (!newHobbyNames.contains(existingHobby.getName()) && !existingHobby.isDeleted()) {
        existingHobby.softDelete();
        eventPublisher.publishEvent(new HobbyRemovedEvent(member.getId(), existingHobby.getName()));
      }
    }
    return hobbies;
  }

  @Transactional
  public Hobby updateHobbyName(Long id, String newName) {
    Hobby hobby = hobbyRepository.findById(id)
        .orElseThrow(HobbyException::hobbyNotFound);
    String oldName = hobby.getName();
    hobby.updateName(newName);
    Member member = memberRepository.findByMemberDetails(hobby.getMemberDetails())
        .orElseThrow(MemberException::memberNotFound);
    eventPublisher.publishEvent(new HobbyNameUpdatedEvent(member.getId(), oldName, newName));
    return hobby;
  }


  @Transactional
  public void deleteHobby(String hobbyName) {
    Member member = memberService.getMemberUsingSecurityContext();
    MemberDetails memberDetails = member.getMemberDetails();
    Hobby targetHobby = hobbyRepository.findByMemberDetailsIdAndName(memberDetails.getId(), hobbyName)
        .orElseThrow(HobbyException::hobbyNotFound);
    targetHobby.softDelete();
    eventPublisher.publishEvent(new HobbyRemovedEvent(member.getId(), hobbyName));
  }
}
