package com.synapps.resona.query.member.event;

public record ReplyUnlikedEvent(
    Long memberId,
    Long replyId
) {
}
