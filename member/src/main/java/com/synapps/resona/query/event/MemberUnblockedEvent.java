package com.synapps.resona.query.event;

public record MemberUnblockedEvent(
    Long blockerId,
    Long unblockedId
) {
}
