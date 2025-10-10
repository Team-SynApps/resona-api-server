package com.synapps.resona.query.member.event;

public record FeedHiddenEvent(
    Long memberId,
    Long feedId
) {
}