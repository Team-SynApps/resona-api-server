package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.dto.response.ProfileDto;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.InvalidTimeStampException;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    /**
     * TODO: 프로필의 언어들 처리를 안하고 데이터베이스에 저장함. 수정해야 함.
     * 회원가입시 생성했던 프로필을 가져와 정보 추가 및 수정하여 반환한다.
     * @param request
     * @return
     */
    @Transactional
    public ProfileDto register(ProfileRequest request) {
        validateData(request);
        String memberEmail = memberService.getMemberEmail();
        Profile profile = memberRepository.findProfileByEmail(memberEmail).orElseThrow(ProfileException::profileNotFound);
        changeProfile(request, profile);

        Profile savedProfile = profileRepository.save(profile);

        return convertToProfileDto(savedProfile);
    }

    @Transactional
    public ProfileDto readProfile() {
        String memberEmail = memberService.getMemberEmail();
        Profile profile = memberRepository.findProfileByEmail(memberEmail).orElseThrow(ProfileException::profileNotFound);

        return convertToProfileDto(profile);
    }

    /**
     * TODO: 프로필의 언어들 처리가 안됨. 수정해야 함
     * @param request
     * @return
     */
    @Transactional
    public ProfileDto editProfile(ProfileRequest request) {
        validateData(request);
        String memberEmail = memberService.getMemberEmail();
        Profile profile = memberRepository.findProfileByEmail(memberEmail).orElseThrow(ProfileException::profileNotFound);
        changeProfile(request, profile);

        return convertToProfileDto(profile);
    }

    /**
     * @return
     */
    @Transactional
    public Profile deleteProfile() {
        String memberEmail = memberService.getMemberEmail();
        Profile profile = memberRepository.findProfileByEmail(memberEmail).orElseThrow(ProfileException::profileNotFound);

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
                    ErrorCode.TIMESTAMP_INVALID.getMessage(),
                    ErrorCode.TIMESTAMP_INVALID.getStatus(),
                    ErrorCode.TIMESTAMP_INVALID.getCode()
            );
        }
    }

    private ProfileDto convertToProfileDto(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .tag(profile.getTag())
                .nickname(profile.getNickname())
                .nationality(profile.getNationality().toString())
                .countryOfResidence(profile.getCountryOfResidence().toString())
                .nativeLanguages(profile.getNativeLanguages().stream().map((Enum::toString)).toList())
                .interestingLanguages(profile.getInterestingLanguages().stream().map((Enum::toString)).toList())
                .profileImageUrl(profile.getProfileImageUrl())
                .backgroundImageUrl(profile.getBackgroundImageUrl())
                .comment(profile.getComment())
                .age(profile.getAge())
                .birth(DateTimeUtil.localDateTimeToString(profile.getBirth()))
                .gender(profile.getGender().toString())
                .build();
    }

    /**
     * Set을 사용하여 받게 되면 불변 컬렉션으로 인지할 수 있게 되어, Hibernate 쪽에 문제가 생길 여지가 있음.
     * 따라서 copyToMutableSet() 을 활용하여 주입
     * @param request
     * @param profile
     */
    private static void changeProfile(ProfileRequest request, Profile profile) {
        profile.modifyProfile(
                request.getNickname(),
                request.getNationality(),
                request.getCountryOfResidence(),
                copyToMutableSet(request.getNativeLanguages()),
                copyToMutableSet(request.getInterestingLanguages()),
                request.getProfileImageUrl(),
                request.getBackgroundImageUrl(),
                request.getBirth(),
                request.getGender(),
                request.getComment());
    }

    private static Set<Language> copyToMutableSet(Set<Language> source) {
        return new HashSet<>(source);
    }
}