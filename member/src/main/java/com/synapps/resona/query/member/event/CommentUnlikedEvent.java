package com.synapps.resona.query.member.event;

public record CommentUnlikedEvent(
    Long memberId,
    Long commentId
) {
}
