package com.synapps.resona.query.event;

public record CommentLikedEvent(
    Long memberId,
    Long commentId
) {
}
