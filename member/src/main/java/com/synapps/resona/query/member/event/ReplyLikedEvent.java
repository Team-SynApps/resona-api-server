package com.synapps.resona.query.member.event;

public record ReplyLikedEvent(
    Long memberId,
    Long replyId
) {
}
