package com.synapps.resona.event;


public record MemberUpdatedEvent(Long memberId, String nickname, String tag, String profileImageUrl) {
}
