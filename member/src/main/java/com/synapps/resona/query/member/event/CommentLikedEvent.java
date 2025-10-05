package com.synapps.resona.query.member.event;

public record CommentLikedEvent(
    Long memberId,
    Long commentId
) {
}
