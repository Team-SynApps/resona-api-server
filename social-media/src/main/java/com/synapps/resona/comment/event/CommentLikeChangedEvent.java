package com.synapps.resona.comment.event;

public record CommentLikeChangedEvent(Long memberId, Long commentId, int delta) {}
