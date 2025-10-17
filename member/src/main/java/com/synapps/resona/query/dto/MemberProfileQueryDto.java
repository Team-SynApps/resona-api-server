package com.synapps.resona.query.dto;

import com.synapps.resona.query.entity.MemberDocument;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileQueryDto {

    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;
    private final String tag;

    public static MemberProfileQueryDto from(MemberDocument memberDocument) {
        return MemberProfileQueryDto.builder()
            .memberId(memberDocument.getId())
            .nickname(memberDocument.getProfile().getNickname())
            .profileImageUrl(memberDocument.getProfile().getProfileImageUrl())
            .tag(memberDocument.getProfile().getTag())
            .build();
    }
}
