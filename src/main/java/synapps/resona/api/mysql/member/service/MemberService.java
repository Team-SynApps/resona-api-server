package synapps.resona.api.mysql.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.utils.DateTimeUtil;
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
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final ProfileRepository profileRepository;
  private final MemberDetailsRepository memberDetailsRepository;
  private final AccountInfoRepository accountInfoRepository;
  private final AuthTokenProvider authTokenProvider;
  private final Logger logger = LogManager.getLogger(MemberService.class);

  private static Set<Language> copyToMutableSet(Set<Language> source) {
    return new HashSet<>(source);
  }

  /**
   * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
   *
   * @return 멤버를 이메일 기준으로 불러옴 Optional 적용 고려
   */
  @Transactional
  public MemberDto getMember() {
    User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    logger.info(userPrincipal.getUsername());
    Member member = memberRepository.findByEmail(userPrincipal.getUsername())
        .orElseThrow(MemberException::memberNotFound);

    return MemberDto.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();
  }

  public String getMemberEmail() {
    User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return userPrincipal.getUsername();
  }

  public Member getMemberUsingSecurityContext() {
    User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return memberRepository.findByEmail(userPrincipal.getUsername())
        .orElseThrow(MemberException::memberNotFound);
  }

  @Transactional
  public MemberInfoDto getMemberDetailInfo() {
    User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Member member = memberRepository.findWithAllRelationsByEmail(userPrincipal.getUsername())
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
    String email = memberPasswordChangeDto.getEmail();
    if (!isCurrentUser(request, email)) {
      throw MemberException.unAuthenticatedRequest();
    }
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(MemberException::memberNotFound);
    member.encodePassword(memberPasswordChangeDto.getChangedPassword());
    return new MemberDto(member.getId(), member.getEmail());
  }

  @Transactional
  public String deleteUser() {
    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Member member = memberRepository.findByEmail(principal.getUsername())
        .orElseThrow(MemberException::memberNotFound);
    member.softDelete();
    memberRepository.save(member);
    return "delete successful";
  }

  public boolean isCurrentUser(HttpServletRequest request, String requestEmail) {
    try {
      String token = resolveToken(request);
//            logger.debug("Resolved token: {}", token);

      if (token == null) {
        logger.debug("Token is null");
        return false;
      }

      AuthToken authToken = authTokenProvider.convertAuthToken(token);
      if (!authToken.validate()) {
        logger.debug("Token validation failed");
        return false;
      }

      Authentication authentication = authTokenProvider.getAuthentication(authToken);
      Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

//            logger.debug("Token Authentication: {}", authentication);
//            logger.debug("Current Authentication: {}", currentAuth);

      if (authentication == null || currentAuth == null) {
        logger.debug("Either authentication or currentAuth is null");
        return false;
      }

      boolean result = authentication.isAuthenticated() &&
          authentication.getName().equals(requestEmail) &&
          authentication.getName().equals(currentAuth.getName());

//            log.debug("isCurrentUser result: {}", result);
      return result;
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

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
