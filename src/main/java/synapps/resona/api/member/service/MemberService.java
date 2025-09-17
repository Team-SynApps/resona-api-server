package synapps.resona.api.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.member.dto.MemberDto;
import synapps.resona.api.member.dto.response.MemberInfoDto;
import synapps.resona.api.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.account.AccountStatus;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member.MemberProvider;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.member.event.MemberUpdatedEvent;
import synapps.resona.api.member.exception.AccountInfoException;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.exception.ProfileException;
import synapps.resona.api.member.repository.member.MemberProviderRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.repository.profile.ProfileRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ProfileRepository profileRepository;
  private final MemberProviderRepository memberProviderRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final Logger logger = LogManager.getLogger(MemberService.class);

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
  public MemberInfoDto getMemberDetailInfo(String email) {
    Member member = memberRepository.findWithAllRelationsByEmail(email)
        .orElseThrow(MemberException::memberNotFound);

    AccountInfo accountInfo = member.getAccountInfo();
    MemberDetails memberDetails = member.getMemberDetails();
    Profile profile = member.getProfile();

    return MemberInfoDto.from(accountInfo, memberDetails, profile);
  }

  @Transactional
  public MemberRegisterResponseDto signUp(RegisterRequest request) {
    final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$";

    if (!request.isSocialLogin()) {
      if (request.getPassword() == null || request.getPassword().isBlank()) {
        throw MemberException.memberPasswordBlank();
      }
      if (!request.getPassword().matches(PASSWORD_PATTERN)) {
        throw MemberException.invalidPasswordPolicy();
      }
    }

    // 이메일로 기존 멤버 조회 (없으면 TempTokenService에서 생성된 임시 계정)
    Member member = memberRepository.findWithRegisterRelationsByEmail(request.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    // 계정 상태 확인 (BANNED, ACTIVE 등)
    checkMemberStatus(member);

    // LOCAL Provider 추가 (이미 있으면 중복 가입으로 간주)
    memberProviderRepository.findByMemberAndProviderType(member, ProviderType.LOCAL)
        .ifPresent(provider -> {
          throw MemberException.duplicateEmail();
        });
    MemberProvider localProvider = MemberProvider.of(member, ProviderType.LOCAL, null);
    member.addProvider(localProvider);

    // 프로필 및 상세 정보 업데이트
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

    // 5. 비밀번호 설정 및 저장
    member.encodePassword(request.getPassword());
    member = memberRepository.save(member);

    // mongoDB 반영
    eventPublisher.publishEvent(new MemberUpdatedEvent(member.getId(), profile));

    return MemberRegisterResponseDto.from(member, profile, memberDetails);
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
      logger.error("Error in isCurrentUser", e);
      return false;
    }
  }

  public boolean isRegisteredMember(String email) {
    return memberRepository.findAccountInfoByEmail(email)
        .map(accountInfo -> !accountInfo.isAccountTemporary())
        .orElse(false);
  }
}