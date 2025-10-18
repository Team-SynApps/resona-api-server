package com.synapps.resona.query.event;

public record ReplyLikedEvent(
    Long memberId,
    Long replyId
) {
}
