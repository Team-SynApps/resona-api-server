package com.synapps.resona.comment.event;

import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.entity.Language;
import java.time.LocalDateTime;
import java.util.List;

public record CommentCreatedEvent(
    Long commentId,
    Long feedId,
    Author author,
    String content,
    Language language,
    LocalDateTime createdAt,
    List<MentionedMember> mentionedMembers
) {
}