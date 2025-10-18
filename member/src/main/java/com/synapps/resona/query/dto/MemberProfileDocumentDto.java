package com.synapps.resona.query.dto;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import com.synapps.resona.entity.Language;
import com.synapps.resona.query.entity.MemberDocument;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileDocumentDto {

    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;
    private final boolean isOnline;
    private final LocalDateTime lastAccessedAt;
    private final LocalDateTime createdAt;
    private final String comment;
    private final int age;
    private final int timezone;
    private final Gender gender;
    private final CountryCode countryCode;
    private final Set<Language> interestedLanguages;
    private final List<String> hobbies;
    private final String tag;

    public static MemberProfileDocumentDto from(MemberDocument memberDocument, Set<Long> onlineMemberIds, LocalDateTime lastAccessedAt) {
        MemberDocument.ProfileEmbed profile = memberDocument.getProfile();
        MemberDocument.MemberDetailsEmbed memberDetails = memberDocument.getMemberDetails();

        return MemberProfileDocumentDto.builder()
            .memberId(memberDocument.getId())
            .nickname(profile.getNickname())
            .profileImageUrl(profile.getProfileImageUrl())
            .isOnline(onlineMemberIds.contains(memberDocument.getId()))
            .lastAccessedAt(lastAccessedAt)
            .createdAt(memberDocument.getCreatedAt())
            .comment(profile.getComment())
            .age(profile.getAge())
            .timezone(memberDetails.getTimezone())
            .gender(profile.getGender())
            .countryCode(profile.getCountryOfResidence())
            .interestedLanguages(profile.getInterestingLanguages())
            .hobbies(memberDetails.getHobbies())
            .tag(profile.getTag())
            .build();
    }
}
