package com.synapps.resona.command.service;

import com.synapps.resona.config.MemberProperties;
import com.synapps.resona.command.dto.MemberDto;
import com.synapps.resona.command.dto.request.auth.RegisterRequest;
import com.synapps.resona.command.dto.request.member.MemberPasswordChangeDto;
import com.synapps.resona.command.dto.response.MemberInfoDto;
import com.synapps.resona.command.dto.response.MemberRegisterResponseDto;
import com.synapps.resona.command.entity.account.AccountInfo;
import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.MemberProvider;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.command.entity.hobby.Hobby;
import com.synapps.resona.command.event.MemberCreatedEvent;
import com.synapps.resona.command.repository.hobby.HobbyRepository;
import com.synapps.resona.event.MemberUpdatedEvent;
import com.synapps.resona.exception.AccountInfoException;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.exception.ProfileException;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.repository.member.FollowRepository;
import com.synapps.resona.command.repository.member.MemberProviderRepository;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.repository.profile.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ProfileRepository profileRepository;
  private final MemberProviderRepository memberProviderRepository;
  private final FollowRepository followRepository;
  private final HobbyRepository hobbyRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final MemberProperties memberProperties;

  private static Set<Language> copyToMutableSet(Set<Language> source) {
    return new HashSet<>(source);
  }

  public String getMemberEmail() {
    UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return userPrincipal.getUsername();
  }

  public Member getMemberUsingSecurityContext() {
    UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return memberRepository.findByEmail(userPrincipal.getUsername())
        .orElseThrow(MemberException::memberNotFound);
  }


  @Transactional
  public MemberRegisterResponseDto signUp(RegisterRequest request) {
    validatePassword(request);

    Member member = findAndValidateMember(request.getEmail());

    new MemberRegistrationHandler(member, request)
        .addLocalProvider()
        .updateProfileAndDetails()
        .encodePassword()
        .save()
        .publishEvents();

    return MemberRegisterResponseDto.from(member, member.getProfile(), member.getMemberDetails());
  }

  private void validatePassword(RegisterRequest request) {
    final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$";
    if (!request.isSocialLogin()) {
      if (request.getPassword() == null || request.getPassword().isBlank()) {
        throw MemberException.memberPasswordBlank();
      }
      if (!request.getPassword().matches(PASSWORD_PATTERN)) {
        throw MemberException.invalidPasswordPolicy();
      }
    }
  }

  private Member findAndValidateMember(String email) {
    Member member = memberRepository.findWithRegisterRelationsByEmail(email)
        .orElseThrow(MemberException::memberNotFound);
    checkMemberStatus(member);
    return member;
  }

  private void publishMemberRegistrationEvents(Member member) {
    // for chat module
    eventPublisher.publishEvent(new MemberUpdatedEvent(member.getId(), member.getProfile().getNickname(), member.getProfile().getTag(), member.getProfile().getProfileImageUrl()));

    // for member query model
    MemberCreatedEvent.AccountInfo accountInfoEvent = new MemberCreatedEvent.AccountInfo(
        member.getAccountInfo().getRoleType(),
        member.getAccountInfo().getStatus()
    );

    MemberCreatedEvent.MemberDetailsInfo memberDetailsInfo = new MemberCreatedEvent.MemberDetailsInfo(
        member.getMemberDetails().getTimezone(),
        member.getMemberDetails().getPhoneNumber(),
        member.getMemberDetails().getMbti(),
        member.getMemberDetails().getAboutMe(),
        member.getMemberDetails().getLocation(),
        hobbyRepository.findAllByMemberDetailsId(member.getMemberDetails().getId()).stream().map(Hobby::getName).collect(Collectors.toList())
    );

    MemberCreatedEvent.ProfileInfo profileInfo = new MemberCreatedEvent.ProfileInfo(
        member.getProfile().getTag(),
        member.getProfile().getNickname(),
        member.getProfile().getNationality(),
        member.getProfile().getCountryOfResidence(),
        member.getProfile().getNativeLanguages(),
        member.getProfile().getInterestingLanguages(),
        member.getProfile().getProfileImageUrl(),
        member.getProfile().getBackgroundImageUrl(),
        member.getProfile().getAge(),
        member.getProfile().getGender(),
        member.getProfile().getComment()
    );

    MemberCreatedEvent createdEvent = new MemberCreatedEvent(
        member.getId(),
        member.getEmail(),
        accountInfoEvent,
        memberDetailsInfo,
        profileInfo,
        member.getProviders().stream().map(MemberProvider::getProviderType).collect(Collectors.toList()),
        member.getLastAccessedAt()
    );

    eventPublisher.publishEvent(createdEvent);
  }

  private void checkMemberStatus(Member member) {
    AccountInfo accountInfo = member.getAccountInfo();
    if (accountInfo == null) {
      throw AccountInfoException.accountInfoNotFound();
    }
    // 차단당한 계정인 경우
    if (accountInfo.getStatus().equals(AccountStatus.BANNED)) {
      throw MemberException.unAuthenticatedRequest();
    }
  }

  @Transactional
  public MemberDto changePassword(HttpServletRequest request,
      MemberPasswordChangeDto memberPasswordChangeDto) {

    if (!isCurrentUser(memberPasswordChangeDto.getEmail())) {
      throw MemberException.unAuthenticatedRequest();
    }
    Member member = memberRepository.findByEmail(memberPasswordChangeDto.getEmail())
        .orElseThrow(MemberException::memberNotFound);
    member.encodePassword(memberPasswordChangeDto.getChangedPassword());
    return MemberDto.of(member.getId(), member.getEmail());
  }

  @Transactional
  public Map<String, String> deleteUser() {
    UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Member member = memberRepository.findByEmail(principal.getUsername())
        .orElseThrow(MemberException::memberNotFound);
    member.softDelete();
    memberRepository.save(member);
    return Map.of("message", "User deleted successfully.");
  }

  public boolean isCurrentUser(String requestEmail) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
        return false;
      }
      UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
      return principal.getUsername().equals(requestEmail);
    } catch (Exception e) {
      log.error("Error in isCurrentUser", e);
      return false;
    }
  }

  public boolean isRegisteredMember(String email) {
    return memberRepository.findAccountInfoByEmail(email)
        .map(accountInfo -> !accountInfo.isAccountTemporary())
        .orElse(false);
  }

  public Member getMember(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
  }

  public Member getMemberWithProfile(Long memberId) {
    return memberRepository.findWithProfileById(memberId).orElseThrow(MemberException::memberNotFound);
  }

  public boolean isCelebrity(Member member) {
    return followRepository.countByFollowing(member) > memberProperties.followerThreshold();
  }

  private class MemberRegistrationHandler {

    private final Member member;
    private final RegisterRequest request;

    public MemberRegistrationHandler(Member member, RegisterRequest request) {
      this.member = member;
      this.request = request;
    }

    public MemberRegistrationHandler addLocalProvider() {
      memberProviderRepository.findByMemberAndProviderType(member, ProviderType.LOCAL)
          .ifPresent(provider -> {
            throw MemberException.duplicateEmail();
          });
      MemberProvider localProvider = MemberProvider.of(member, ProviderType.LOCAL, null);
      member.addProvider(localProvider);
      return this;
    }

    public MemberRegistrationHandler updateProfileAndDetails() {
      Profile profile = member.getProfile();
      if (profileRepository.existsByTag(request.getTag())) {
        throw ProfileException.duplicateTag();
      }
      profile.registerTag(request.getTag());

      MemberDetails memberDetails = member.getMemberDetails();
      AccountInfo accountInfo = member.getAccountInfo();

      profile.join(
          request.getNickname(),
          request.getNationality(),
          request.getCountryOfResidence(),
          copyToMutableSet(request.getNativeLanguageCodes().stream().map(Language::fromCode).collect(
              Collectors.toSet())),
          copyToMutableSet(request.getInterestingLanguageCodes().stream().map(Language::fromCode).collect(
              Collectors.toSet())),
          request.getProfileImageUrl(),
          request.getBirth());
      memberDetails.join(request.getTimezone());
      accountInfo.join();
      return this;
    }

    public MemberRegistrationHandler encodePassword() {
      member.encodePassword(request.getPassword());
      return this;
    }

    public MemberRegistrationHandler save() {
      memberRepository.save(member);
      return this;
    }

    public void publishEvents() {
      publishMemberRegistrationEvents(member);
    }
  }
}