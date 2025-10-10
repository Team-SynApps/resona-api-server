package com.synapps.resona.query.member.event;

public record MemberBlockedEvent(
    Long blockerId,
    Long blockedId
) {
}