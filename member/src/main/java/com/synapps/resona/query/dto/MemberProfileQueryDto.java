package com.synapps.resona.query.dto;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.Language;
import com.synapps.resona.query.entity.MemberDocument;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileQueryDto {

    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;
    private final String tag;
    private final CountryCode nationality;
    private final CountryCode countryOfResidence;
    private final Set<Language> nativeLanguages;
    private final Set<Language> interestingLanguages;
    private final String backgroundImageUrl;
    private final Integer age;
    private final Gender gender;
    private final String comment;

    public static MemberProfileQueryDto from(MemberDocument memberDocument) {
        MemberDocument.ProfileEmbed profile = memberDocument.getProfile();
        return MemberProfileQueryDto.builder()
            .memberId(memberDocument.getId())
            .nickname(profile.getNickname())
            .profileImageUrl(profile.getProfileImageUrl())
            .tag(profile.getTag())
            .nationality(profile.getNationality())
            .countryOfResidence(profile.getCountryOfResidence())
            .nativeLanguages(profile.getNativeLanguages())
            .interestingLanguages(profile.getInterestingLanguages())
            .backgroundImageUrl(profile.getBackgroundImageUrl())
            .age(profile.getAge())
            .gender(profile.getGender())
            .comment(profile.getComment())
            .build();
    }
}
