package synapps.resona.api.mysql.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.dto.response.MemberInfoDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.MemberProvider;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.AccountInfoException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.account.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.member.MemberProviderRepository;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.member.repository.member_details.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.profile.ProfileRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ProfileRepository profileRepository;
  private final MemberDetailsRepository memberDetailsRepository;
  private final AccountInfoRepository accountInfoRepository;
  private final MemberProviderRepository memberProviderRepository; // Repository 주입
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
    // 이메일로 기존 멤버 조회 (없으면 TempTokenService에서 생성된 임시 계정)
    Member member = memberRepository.findWithAccountInfoByEmail(request.getEmail())
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
        copyToMutableSet(request.getNativeLanguages()),
        copyToMutableSet(request.getInterestingLanguages()),
        request.getProfileImageUrl(),
        request.getBirth());
    memberDetails.join(request.getTimezone());
    accountInfo.join();

    // 5. 비밀번호 설정 및 저장
    member.encodePassword(request.getPassword());
    memberRepository.save(member);

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