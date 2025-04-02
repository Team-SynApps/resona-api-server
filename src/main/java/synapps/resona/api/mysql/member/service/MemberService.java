package synapps.resona.api.mysql.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.dto.request.auth.DuplicateIdRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberInfoDto;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.AccountInfoException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.entity.ProviderType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenProvider authTokenProvider;
    private final Logger logger = LogManager.getLogger(MemberService.class);

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     *
     * @return 멤버를 이메일 기준으로 불러옴
     * Optional 적용 고려
     */
    @Transactional
    public MemberDto getMember() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(userPrincipal.getUsername());
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(MemberException::memberNotFound);

        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .build();
    }

    public String getMemberEmail() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getUsername();
    }

    public Member getMemberUsingSecurityContext() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(MemberException::memberNotFound);
    }

    @Transactional
    public MemberInfoDto getMemberDetailInfo() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findWithAllRelationsByEmail(userPrincipal.getUsername())
                .orElseThrow(MemberException::memberNotFound);

        AccountInfo accountInfo = member.getAccountInfo();
        MemberDetails memberDetails = member.getMemberDetails();
        Profile profile = member.getProfile();

        return buildMemberInfoDto(accountInfo, memberDetails, profile);
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private Integer nullToZero(Integer value) {
        return value != null ? value : 0;
    }

    @Transactional
    public MemberRegisterResponseDto signUp(RegisterRequest request) {
        checkMemberStatus(request);

        Profile newProfile = Profile.of(
                request.getNationality(),
                request.getCountryOfResidence(),
                copyToMutableSet(request.getNativeLanguages()),
                copyToMutableSet(request.getInterestingLanguages()),
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getBirth()
        );

        MemberDetails newMemberDetails = MemberDetails.of(
                request.getTimezone()
        );

        AccountInfo newAccountInfo = AccountInfo.of(
                RoleType.USER,
                ProviderType.LOCAL,
                AccountStatus.ACTIVE
        );

        Member member = Member.of(
                newAccountInfo,
                newMemberDetails,
                newProfile,
                request.getEmail(),
                request.getPassword(),
                LocalDateTime.now() // lastAccessedAt
        );

        member.encodePassword(request.getPassword());
        memberRepository.save(member);

        return MemberRegisterResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nationality(newProfile.getNationality())
                .countryOfResidence(newProfile.getCountryOfResidence())
                .nativeLanguages(newProfile.getNativeLanguages())
                .interestingLanguages(newProfile.getInterestingLanguages())
                .birth(newProfile.getBirth().toString())
                .nickname(newProfile.getNickname())
                .profileImageUrl(newProfile.getProfileImageUrl())
                .timezone(newMemberDetails.getTimezone())
                .build();
    }

    private void checkMemberStatus(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            AccountInfo accountInfo = memberRepository.findAccountInfoByEmail(request.getEmail()).orElseThrow(AccountInfoException::accountInfoNotFound);
            // 차단당한 계정인 경우
            if (accountInfo.getStatus().equals(AccountStatus.BANNED)) {
                throw MemberException.unAuthenticatedRequest();
            }
            // 이미 활성화된 계정인 경우
            if (accountInfo.getStatus().equals(AccountStatus.ACTIVE)) {
                throw MemberException.duplicateEmail();
            }
        }
    }

    public boolean checkDuplicateId(DuplicateIdRequest request) throws Exception {
        return memberRepository.existsById(Long.parseLong(request.getId()));
    }

    @Transactional
    public MemberDto changePassword(HttpServletRequest request, MemberPasswordChangeDto memberPasswordChangeDto) {
        String email = memberPasswordChangeDto.getEmail();
        if (!isCurrentUser(request, email)) {
            throw MemberException.unAuthenticatedRequest();
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberException::memberNotFound);
        member.encodePassword(memberPasswordChangeDto.getChangedPassword());
        return new MemberDto(member.getId(), member.getEmail());
    }

    @Transactional
    public String deleteUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(principal.getUsername()).orElseThrow(MemberException::memberNotFound);
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
        AccountInfo accountInfo = memberRepository.findAccountInfoByEmail(email).orElseThrow(AccountInfoException::accountInfoNotFound);
        return !accountInfo.isAccountTemporary();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private static Set<Language> copyToMutableSet(Set<Language> source) {
        return new HashSet<>(source);
    }

    private MemberInfoDto buildMemberInfoDto(AccountInfo accountInfo, MemberDetails memberDetails, Profile profile) {
        return MemberInfoDto.builder()
                // Account Info
                .roleType(nullToEmpty(accountInfo != null ? accountInfo.getRoleType().toString() : null))
                .accountStatus(nullToEmpty(accountInfo != null ? accountInfo.getStatus().toString() : null))
                .providerType(nullToEmpty(accountInfo != null ? accountInfo.getProviderType().toString() : null))

                // Member Details
                .timezone(nullToZero(memberDetails != null ? memberDetails.getTimezone() : null))
                .phoneNumber(nullToEmpty(memberDetails != null ? memberDetails.getPhoneNumber() : null))
                .location(nullToEmpty(memberDetails != null ? memberDetails.getLocation() : null))
                .mbti(nullToEmpty(memberDetails != null ? memberDetails.getMbti() != null ? memberDetails.getMbti().toString() : null : null))
                .aboutMe(nullToEmpty(memberDetails != null ? memberDetails.getAboutMe() : null))

                // Profile
                .nickname(nullToEmpty(profile != null ? profile.getNickname() : null))
                .tag(nullToEmpty(profile != null ? profile.getTag() : null))
                .nationality(nullToEmpty(profile != null ? profile.getNationality().toString() : null))
                .countryOfResidence(nullToEmpty(profile != null ? profile.getCountryOfResidence().toString() : null))
                .nativeLanguages(profile != null ? profile.getNativeLanguages() : null)
                .interestingLanguages(profile != null ? profile.getInterestingLanguages() : null)
                .profileImageUrl(nullToEmpty(profile != null ? profile.getProfileImageUrl() : null))
                .backgroundImageUrl(nullToEmpty(profile != null ? profile.getBackgroundImageUrl() : null))
                .birth(profile != null ? DateTimeUtil.localDateTimeToStringSimpleFormat(profile.getBirth()) : null)
                .age(nullToZero(profile != null ? profile.getAge() : null))
                .gender(nullToEmpty(profile != null ? profile.getGender() != null ? profile.getGender().toString() : null : null))
                .comment(nullToEmpty(profile != null ? profile.getComment() : null))
                .build();
    }
}
