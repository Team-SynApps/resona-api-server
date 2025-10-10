package com.synapps.resona.comment.query.dto;

import java.util.Set;

public record ViewerContext(
    Long viewerId,
    Set<Long> hiddenCommentIds,
    Set<Long> hiddenReplyIds,
    Set<Long> blockedMemberIds
) {
}