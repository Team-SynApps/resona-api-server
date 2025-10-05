package com.synapps.resona.comment.event;

public record ReplyLikeChangedEvent(Long memberId, Long replyId, Long parentCommentId, int delta) {}
