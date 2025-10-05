package com.synapps.resona.comment.query.service;

import java.util.Set;

public record ViewerContext(
    Long viewerId,
    Set<Long> hiddenCommentIds,
    Set<Long> hiddenReplyIds,
    Set<Long> blockedMemberIds
) {
}