package com.synapps.resona.query.dto;

import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.query.entity.MemberDocument;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailsQueryDto {

    private final Integer timezone;
    private final String phoneNumber;
    private final MBTI mbti;
    private final String aboutMe;
    private final String location;
    private final List<String> hobbies;

    public static MemberDetailsQueryDto from(MemberDocument.MemberDetailsEmbed embed) {
        return MemberDetailsQueryDto.builder()
            .timezone(embed.getTimezone())
            .phoneNumber(embed.getPhoneNumber())
            .mbti(embed.getMbti())
            .aboutMe(embed.getAboutMe())
            .location(embed.getLocation())
            .hobbies(embed.getHobbies())
            .build();
    }
}
