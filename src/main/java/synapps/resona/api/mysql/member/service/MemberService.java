package synapps.resona.api.mysql.member.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.dto.request.auth.DuplicateIdRequest;
import synapps.resona.api.mysql.member.dto.request.auth.SignupRequest;
import synapps.resona.api.mysql.member.dto.request.member.MemberPasswordChangeDto;
import synapps.resona.api.mysql.member.dto.response.MemberDetailInfoDto;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final MemberDetailsRepository memberDetailsRepository;
    private final ProfileRepository profileRepository;
    private final AuthTokenProvider authTokenProvider;

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     * @return 멤버를 이메일 기준으로 불러옴
     * Optional 적용 고려
     */
    @Transactional
    public MemberDto getMember() {
        log.info("get member");
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userPrincipal.getUsername());
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(MemberException::memberNotFound);
        AccountInfo accountInfo = accountInfoRepository.findByMember(member);
        accountInfo.updateLastAccessedAt();

        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .build();
    }

    @Transactional
    public MemberDetailInfoDto getMemberDetailInfo() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(MemberException::memberNotFound);

        AccountInfo accountInfo = accountInfoRepository.findByMember(member);
        accountInfo.updateLastAccessedAt();

        MemberDetails memberDetails = memberDetailsRepository.findByMember(member).orElse(null);
        Profile profile = profileRepository.findByMember(member).orElse(null);

        return MemberDetailInfoDto.builder()
                // Account Info
                .roleType(nullToEmpty(accountInfo != null ? accountInfo.getRoleType().toString() : null))
                .accountStatus(nullToEmpty(accountInfo != null ? accountInfo.getStatus().toString() : null))
                .lastAccessedAt(accountInfo != null ? DateTimeUtil.localDateTimeToString(accountInfo.getLastAccessedAt()) : null)
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

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private Integer nullToZero(Integer value) {
        return value != null ? value : 0;
    }

    @Transactional
    public MemberDto signUp(SignupRequest request) throws Exception {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw MemberException.duplicateEmail();
        }

        Member member = Member.of(
                request.getEmail(),
                request.getPassword(),
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // modifiedAt
        );

        AccountInfo accountInfo = AccountInfo.of(
                member,
                RoleType.USER,
                ProviderType.LOCAL,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        member.encodePassword(request.getPassword());
        memberRepository.save(member);
        accountInfoRepository.save(accountInfo);
        return new MemberDto(member.getId(), member.getEmail());
    }

    public boolean checkDuplicateId(DuplicateIdRequest request) throws Exception {
        return memberRepository.existsById(Long.parseLong(request.getId()));
    }

    @Transactional
    public MemberDto changePassword(HttpServletRequest request, MemberPasswordChangeDto memberPasswordChangeDto) {
        String email = memberPasswordChangeDto.getEmail();
        if(!isCurrentUser(request, email)){
            throw MemberException.unAuthenticatedRequest();
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberException::memberNotFound);
        member.encodePassword(memberPasswordChangeDto.getChangedPassword());
        return new MemberDto(member.getId(), member.getEmail());
    }

    public String deleteUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(principal.getUsername()).orElseThrow(MemberException::memberNotFound);
        memberRepository.delete(member);
        return "delete successful";
    }

    public boolean isCurrentUser(HttpServletRequest request, String requestEmail) {
        try {
            String token = resolveToken(request);
            log.debug("Resolved token: {}", token);

            if (token == null) {
                log.debug("Token is null");
                return false;
            }

            AuthToken authToken = authTokenProvider.convertAuthToken(token);
            if (!authToken.validate()) {
                log.debug("Token validation failed");
                return false;
            }

            Authentication authentication = authTokenProvider.getAuthentication(authToken);
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            log.debug("Token Authentication: {}", authentication);
            log.debug("Current Authentication: {}", currentAuth);

            if (authentication == null || currentAuth == null) {
                log.debug("Either authentication or currentAuth is null");
                return false;
            }

            boolean result = authentication.isAuthenticated() &&
                    authentication.getName().equals(requestEmail) &&
                    authentication.getName().equals(currentAuth.getName());

            log.debug("isCurrentUser result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in isCurrentUser", e);
            return false;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
