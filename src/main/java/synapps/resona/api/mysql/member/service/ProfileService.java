package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.dto.response.ProfileDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Gender;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.InvalidTimeStampException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberService memberService;

    /**
     * TODO: 프로필의 언어들 처리를 안하고 데이터베이스에 저장함. 수정해야 함.
     * @param request
     * @return
     */
    @Transactional
    public ProfileDto register(ProfileRequest request) {
        validateData(request);
        Member member = memberService.getMemberUsingSecurityContext();

        Profile newProfile = createProfileFromRequest(member, request);
        Profile savedProfile = profileRepository.save(newProfile);

        return convertToProfileDto(savedProfile);
    }

    @Transactional
    public ProfileDto getProfile() {
        Long memberId = memberService.getMember().getId();
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

        return convertToProfileDto(profile);
    }

    /**
     * TODO: 프로필의 언어들 처리가 안되서 들어옴 수정해야 함.
     * @param memberId
     * @return
     */
    @Transactional
    public ProfileDto getProfileByMemberId(Long memberId) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

        return convertToProfileDto(profile);
    }

    /**
     * TODO: 프로필의 언어들 처리가 안됨. 수정해야 함.
     * TODO: 커스텀 쿼리 작성 필요. 유저 이메일로 프로필을 가져오는 쿼리 작성하는게 제일 좋음.
     * @param request
     * @return
     */
    @Transactional
    public ProfileDto editProfile(ProfileRequest request) {
        validateData(request);

        Long memberId = memberService.getMemberUsingSecurityContext().getId();

        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

        updateProfileFromRequest(profile, request);

        return convertToProfileDto(profile);
    }

    /**
     * TODO: 프로필 언어들 처리가 안됨. 수정해야 함.
     * @return
     */
    @Transactional
    public Profile deleteProfile() {
        Long memberId = memberService.getMember().getId();
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

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

    private Set<Language> convertLanguages(List<String> primitiveLanguages) {
        Set<Language> languages = new HashSet<>();
        for (String primitive : primitiveLanguages) {
            Language language = Language.fromCode(primitive);
            languages.add(language);
        }
        return languages;
    }

    private Profile createProfileFromRequest(Member member, ProfileRequest request) {
        return Profile.of(
                member,
                request.getNickname(),
                CountryCode.fromCode(request.getNationality()),
                CountryCode.fromCode(request.getCountryOfResidence()),
                convertLanguages(request.getNativeLanguages()),
                convertLanguages(request.getInterestingLanguages()),
                request.getProfileImageUrl(),
                request.getBackgroundImageUrl(),
                request.getBirth(),
                Gender.of(request.getGender()),
                request.getComment()
        );
    }

    private void updateProfileFromRequest(Profile profile, ProfileRequest request) {
        profile.modifyProfile(
                request.getNickname(),
                CountryCode.fromCode(request.getNationality()),
                CountryCode.fromCode(request.getCountryOfResidence()),
                convertLanguages(request.getNativeLanguages()),
                convertLanguages(request.getInterestingLanguages()),
                request.getProfileImageUrl(),
                request.getBackgroundImageUrl(),
                request.getBirth(),
                Gender.of(request.getGender()),
                request.getComment()
        );
    }

    private ProfileDto convertToProfileDto(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .memberId(profile.getMember().getId())
                .tag(profile.getTag())
                .nickname(profile.getNickname())
                .nationality(profile.getNationality().toString())
                .countryOfResidence(profile.getCountryOfResidence().toString())
//                .nativeLanguages(profile.getNativeLanguages().stream().map((Enum::toString)).toList())
//                .interestingLanguages(profile.getInterestingLanguages().stream().map((Enum::toString)).toList())
                .profileImageUrl(profile.getProfileImageUrl())
                .backgroundImageUrl(profile.getBackgroundImageUrl())
                .comment(profile.getComment())
                .age(profile.getAge())
                .birth(DateTimeUtil.localDateTimeToString(profile.getBirth()))
                .gender(profile.getGender().toString())
                .build();
    }
}