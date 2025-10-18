package com.synapps.resona.command.event;

import com.synapps.resona.command.entity.member_details.MBTI;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDetailsUpdatedEvent {

    private final Long memberId;
    private final Integer timezone;
    private final String phoneNumber;
    private final MBTI mbti;
    private final String aboutMe;
    private final String location;
}
