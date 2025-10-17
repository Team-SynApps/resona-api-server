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
public class ProfileQueryResponseDto {

    private final String tag;
    private final String nickname;
    private final CountryCode nationality;
    private final CountryCode countryOfResidence;
    private final Set<Language> nativeLanguages;
    private final Set<Language> interestingLanguages;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final Integer age;
    private final Gender gender;
    private final String comment;

    public static ProfileQueryResponseDto from(MemberDocument.ProfileEmbed embed) {
        return ProfileQueryResponseDto.builder()
            .tag(embed.getTag())
            .nickname(embed.getNickname())
            .nationality(embed.getNationality())
            .countryOfResidence(embed.getCountryOfResidence())
            .nativeLanguages(embed.getNativeLanguages())
            .interestingLanguages(embed.getInterestingLanguages())
            .profileImageUrl(embed.getProfileImageUrl())
            .backgroundImageUrl(embed.getBackgroundImageUrl())
            .age(embed.getAge())
            .gender(embed.getGender())
            .comment(embed.getComment())
            .build();
    }
}
