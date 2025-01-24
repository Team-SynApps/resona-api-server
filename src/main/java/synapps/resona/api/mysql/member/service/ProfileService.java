package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRegisterRequest;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.ProfileRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;

    /**
     * 필요한 함수
     * - 프로필 등록(부분 등록 가능하게 할건지, 기본 프로필 설정)
     * - 프로필 읽기
     * - 프로필 수정
     * - 프로필 삭제
     */

    @Transactional
    public Profile register(ProfileRegisterRequest request) {
        Long memberId = request.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
        try{
            Profile newProfile = Profile.of(member, request.getNickname(), request.getUsingLanguages(), request.getProfileImageUrl(), request.getBackgroundImageUrl(), request.getMbti(), request.getComment(), request.getAboutMe(), LocalDateTime.now(), LocalDateTime.now());
            profileRepository.save(newProfile);
            return newProfile;
        } catch (ProfileException e) {
            throw e;
        }
    }

    public Profile getProfile() {
        Long memberId = memberService.getMember().getId();
        return profileRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
    }

    @Transactional
    public Profile editProfile(ProfileRegisterRequest request) {
        Long memberId = request.getMemberId();
        Profile profile = profileRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
        profile.modifyProfile(request.getNickname(), request.getUsingLanguages(), request.getProfileImageUrl(), request.getBackgroundImageUrl(), request.getMbti(), request.getComment(), request.getAboutMe());
        return profile;
    }

    @Transactional
    public Profile deleteProfile() {
        Long memberId = memberService.getMember().getId();
        Profile profile = profileRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);
        profile.softDelete();
        return profile;
    }

}
