package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.mysql.member.dto.request.personal_info.PersonalInfoRequest;
import synapps.resona.api.mysql.member.entity.CountryCode;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.personal_info.PersonalInfo;
import synapps.resona.api.mysql.member.exception.InvalidTimeStampException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.PersonalInfoRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final PersonalInfoRepository personalInfoRepository;
    private final MemberService memberService;

    /**
     * PersonalInfo 등록
     */
    @Transactional
    public PersonalInfo register(PersonalInfoRequest request) {
        validateData(request);
        Member member = memberService.getMember();

        PersonalInfo personalInfo = PersonalInfo.of(
                member,
                request.getTimezone(),
                CountryCode.fromCode(request.getNationality()),
                CountryCode.fromCode(request.getCountryOfResidence()),
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
        validateData(request);
        Member member = memberService.getMember();
        PersonalInfo personalInfo = personalInfoRepository.findByMember(member).orElseThrow(MemberException::memberNotFound);

        personalInfo.updatePersonalInfo(
                request.getTimezone(),
                CountryCode.fromCode(request.getNationality()),
                CountryCode.fromCode(request.getCountryOfResidence()),
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

    private void validateData(PersonalInfoRequest request) {
        validateTimeStamp(request.getBirth());
    }

    private void validateTimeStamp(String timestamp) {
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!timestamp.matches(regex)) {
            throw InvalidTimeStampException.of(ErrorCode.TIMESTAMP_INVALID.getMessage(), ErrorCode.TIMESTAMP_INVALID.getStatus(), ErrorCode.TIMESTAMP_INVALID.getCode());
        }
    }
}

