package com.synapps.resona.query.member.event;

public record MemberUnblockedEvent(
    Long blockerId,
    Long unblockedId
) {
}
