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
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.AccountInfoException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.account.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.member.repository.member_details.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.profile.ProfileRepository;
import synapps.resona.api.oauth.entity.UserPrincipal; // 수정: UserPrincipal import

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ProfileRepository profileRepository;
  private final MemberDetailsRepository memberDetailsRepository;
  private final AccountInfoRepository accountInfoRepository;
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
    checkMemberStatus(request);
    Member member = memberRepository.findWithAllRelationsByEmail(request.getEmail())
        .orElseThrow(MemberException::memberNotFound);

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

    member.encodePassword(request.getPassword());
    memberRepository.save(member);
    profileRepository.save(profile);
    memberDetailsRepository.save(memberDetails);
    accountInfoRepository.save(accountInfo);

    return MemberRegisterResponseDto.from(member, profile, memberDetails);
  }

  private void checkMemberStatus(RegisterRequest request) {
    boolean isMemberExists = memberRepository.existsByEmail(request.getEmail());
    if (!isMemberExists) {
      throw MemberException.memberNotFound();
    }
    AccountInfo accountInfo = memberRepository.findAccountInfoByEmail(request.getEmail())
        .orElseThrow(AccountInfoException::accountInfoNotFound);
    // 차단당한 계정인 경우
    if (accountInfo.getStatus().equals(AccountStatus.BANNED)) {
      throw MemberException.unAuthenticatedRequest();
    }
    // 이미 활성화된 계정인 경우
    if (accountInfo.getStatus().equals(AccountStatus.ACTIVE)) {
      throw MemberException.duplicateEmail();
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

  /**
   * isCurrentUser 로직을 SecurityContext를 직접 활용하도록 대폭 수정 및 단순화
   * @param requestEmail 요청으로 들어온 이메일
   * @return 현재 인증된 사용자의 이메일과 일치하는지 여부
   */
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
    AccountInfo accountInfo = memberRepository.findAccountInfoByEmail(email)
        .orElseThrow(AccountInfoException::accountInfoNotFound);
    return !accountInfo.isAccountTemporary();
  }
}