package com.synapps.resona.command.service;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.command.dto.MemberDto;
import com.synapps.resona.command.dto.request.profile.ProfileRequest;
import com.synapps.resona.command.dto.response.ProfileResponse;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.command.event.ProfileUpdatedEvent;
import com.synapps.resona.event.MemberUpdatedEvent;
import com.synapps.resona.exception.InvalidTimeStampException;
import com.synapps.resona.exception.ProfileException;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.repository.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  private static void changeProfile(ProfileRequest request, Profile profile) {
    profile.modifyProfile(
        request.getNickname(),
        request.getNationality(),
        request.getCountryOfResidence(),
        copyToMutableLanguageSet(request.getNativeLanguageCodes()),
        copyToMutableLanguageSet(request.getInterestingLanguageCodes()),
        request.getProfileImageUrl(),
        request.getBackgroundImageUrl(),
        request.getBirth(),
        request.getGender(),
        request.getComment());
  }

  private static Set<Language> copyToMutableLanguageSet(Set<String> source) {
    return source.stream().map(Language::fromCode).collect(Collectors.toSet());
  }

  @Transactional
  public ProfileResponse register(ProfileRequest request, MemberDto memberInfo) {
    validateData(request);
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);
    changeProfile(request, profile);

    Profile savedProfile = profileRepository.save(profile);

    publishProfileEvents(memberInfo.getId(), savedProfile);
    return ProfileResponse.from(savedProfile);
  }

  @Transactional
  public ProfileResponse editProfile(ProfileRequest request, MemberDto memberInfo) {
    validateData(request);
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);
    changeProfile(request, profile);

    publishProfileEvents(memberInfo.getId(), profile);
    return ProfileResponse.from(profile);
  }

  @Transactional
  public Profile deleteProfile(MemberDto memberInfo) {
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);

    profile.softDelete();
    return profile;
  }

  private void validateData(ProfileRequest request) {
    validateTimeStamp(request.getBirth());
  }

  private void validateTimeStamp(String timestamp) {
    String regex = "^\\d{4}-\\d{2}-\\d{2}$";
    if (!timestamp.matches(regex)) {
      throw InvalidTimeStampException.of(
          MemberErrorCode.TIMESTAMP_INVALID.getMessage(),
          MemberErrorCode.TIMESTAMP_INVALID.getStatus(),
          MemberErrorCode.TIMESTAMP_INVALID.getCustomCode()
      );
    }
  }

  private void publishProfileEvents(Long memberId, Profile profile) {
    eventPublisher.publishEvent(new MemberUpdatedEvent(memberId, profile.getNickname(), profile.getTag(), profile.getProfileImageUrl()));
    eventPublisher.publishEvent(new ProfileUpdatedEvent(
        memberId,
        profile.getTag(),
        profile.getNickname(),
        profile.getNationality(),
        profile.getCountryOfResidence(),
        profile.getNativeLanguages(),
        profile.getInterestingLanguages(),
        profile.getProfileImageUrl(),
        profile.getBackgroundImageUrl(),
        profile.getAge(),
        profile.getGender(),
        profile.getComment()
    ));
  }
}