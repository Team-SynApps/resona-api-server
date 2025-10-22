package com.synapps.resona.comment.query.dto;

import java.util.Set;

public record CommentViewerContext(
    Long viewerId,
    Set<Long> hiddenCommentIds,
    Set<Long> hiddenReplyIds,
    Set<Long> blockedMemberIds,
    Set<Long> likedCommentIds,
    Set<Long> likedReplyIds
) {
}