package com.synapps.resona.query.event;

public record MemberBlockedEvent(
    Long blockerId,
    Long blockedId
) {
}