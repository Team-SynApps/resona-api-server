package com.synapps.resona.query.event;

public record ReplyUnlikedEvent(
    Long memberId,
    Long replyId
) {
}
