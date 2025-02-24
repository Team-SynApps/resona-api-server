package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRegisterRequest;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.InvalidTimeStampException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;

    @Transactional
    public Profile register(ProfileRegisterRequest request) {
        validateData(request);

        Long memberId = request.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException::memberNotFound);

        try {
            Profile newProfile = Profile.of(
                    member,
                    request.getNickname(),
                    CountryCode.fromCode(request.getNationality()),
                    CountryCode.fromCode(request.getCountryOfResidence()),
                    convertLanguages(request.getNativeLanguages()),
                    convertLanguages(request.getInterestingLanguages()),
                    request.getProfileImageUrl(),
                    request.getBackgroundImageUrl(),
                    request.getBirth(),
                    request.getGender(),
                    request.getComment()
            );

            return profileRepository.save(newProfile);
        } catch (Exception e) {
            throw ProfileException.invalidProfile();
        }
    }

    public Profile getProfile() {
        Long memberId = memberService.getMember().getId();
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);
    }

    public Profile getProfileByMemberId(Long memberId) {
        return profileRepository.findByMemberId(memberId).orElseThrow(MemberException::memberNotFound);
    }

    @Transactional
    public Profile editProfile(ProfileRegisterRequest request) {
        validateData(request);

        Long memberId = request.getMemberId();
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

        profile.modifyProfile(
                request.getNickname(),
                CountryCode.fromCode(request.getNationality()),
                CountryCode.fromCode(request.getCountryOfResidence()),
                convertLanguages(request.getNativeLanguages()),
                convertLanguages(request.getInterestingLanguages()),
                request.getProfileImageUrl(),
                request.getBackgroundImageUrl(),
                request.getBirth(),
                request.getGender(),
                request.getComment()
        );

        return profile;
    }

    @Transactional
    public Profile deleteProfile() {
        Long memberId = memberService.getMember().getId();
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(MemberException::memberNotFound);

        profile.softDelete();
        return profile;
    }

    private void validateData(ProfileRegisterRequest request) {
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
}