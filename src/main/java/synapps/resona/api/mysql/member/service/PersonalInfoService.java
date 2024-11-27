package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.personal_info.PersonalInfoRequest;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.personal_info.PersonalInfo;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.repository.PersonalInfoRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final MemberRepository memberRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final MemberService memberService;

    /**
     * PersonalInfo 등록
     */
    @Transactional
    public PersonalInfo register(PersonalInfoRequest request) {
        Long memberId = request.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException::memberNotFound);

        PersonalInfo personalInfo = PersonalInfo.of(
                member,
                request.getTimezone(),
                request.getNationality(),
                request.getCountryOfResidence(),
                request.getPhoneNumber(),
                request.getBirth(),
                request.getGender(),
                request.getLocation(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        personalInfoRepository.save(personalInfo);
        return personalInfo;
    }

    /**
     * PersonalInfo 조회
     */
    public PersonalInfo getPersonalInfo() {
        Member member = memberService.getMember();
        return personalInfoRepository.findByMember(member).orElseThrow(MemberException::memberNotFound);
    }

    /**
     * PersonalInfo 수정
     */
    @Transactional
    public PersonalInfo editPersonalInfo(PersonalInfoRequest request) {
        Member member = memberService.getMember();
        PersonalInfo personalInfo = personalInfoRepository.findByMember(member).orElseThrow(MemberException::memberNotFound);

        personalInfo.updatePersonalInfo(
                request.getTimezone(),
                request.getNationality(),
                request.getCountryOfResidence(),
                request.getPhoneNumber(),
                request.getBirth(),
                request.getGender(),
                request.getLocation()
        );

        return personalInfo;
    }

    /**
     * PersonalInfo 삭제
     */
    @Transactional
    public PersonalInfo deletePersonalInfo() {
        Member member = memberService.getMember();
        PersonalInfo personalInfo = personalInfoRepository.findByMember(member).orElseThrow(MemberException::memberNotFound);

        personalInfo.softDelete();
        return personalInfo;
    }
}

