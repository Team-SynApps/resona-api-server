package com.synapps.resona.query.event;

public record CommentUnlikedEvent(
    Long memberId,
    Long commentId
) {
}
