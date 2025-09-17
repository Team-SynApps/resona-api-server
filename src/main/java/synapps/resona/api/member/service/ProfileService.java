package synapps.resona.api.member.service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import synapps.resona.api.member.code.MemberErrorCode;
import synapps.resona.api.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.member.dto.MemberDto;
import synapps.resona.api.member.dto.response.ProfileResponse;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.member.event.MemberUpdatedEvent;
import synapps.resona.api.member.exception.InvalidTimeStampException;
import synapps.resona.api.member.exception.ProfileException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.repository.profile.ProfileRepository;


@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Set을 사용하여 받게 되면 불변 컬렉션으로 인지할 수 있게 되어, Hibernate 쪽에 문제가 생길 여지가 있음. 따라서 copyToMutableSet() 을 활용하여
   * 주입
   *
   * @param request
   * @param profile
   */
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

  /**
   * TODO: 프로필의 언어들 처리를 안하고 데이터베이스에 저장함. 수정해야 함.
   * 회원가입시 생성했던 프로필을 가져와 정보 추가 및 수정하여 반환한다.
   *
   * @param request
   * @return
   */
  @Transactional
  public ProfileResponse register(ProfileRequest request, MemberDto memberInfo) {
    validateData(request);
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);
    changeProfile(request, profile);

    Profile savedProfile = profileRepository.save(profile);

    eventPublisher.publishEvent(new MemberUpdatedEvent(memberInfo.getId(), savedProfile));
    return ProfileResponse.from(savedProfile);
  }

  @Transactional
  public ProfileResponse readProfile(MemberDto memberInfo) {
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);

    return ProfileResponse.from(profile);
  }

  /**
   * TODO: 프로필의 언어들 처리가 안됨. 수정해야 함
   *
   * @param request
   * @return
   */
  @Transactional
  public ProfileResponse editProfile(ProfileRequest request, MemberDto memberInfo) {
    validateData(request);
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);
    changeProfile(request, profile);

    return ProfileResponse.from(profile);
  }

  /**
   * @return
   */
  @Transactional
  public Profile deleteProfile(MemberDto memberInfo) {
    Profile profile = memberRepository.findProfileByEmail(memberInfo.getEmail())
        .orElseThrow(ProfileException::profileNotFound);

    profile.softDelete();
    return profile;
  }

  public boolean checkDuplicateTag(String tag) {
    return profileRepository.existsByTag(tag);
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
}