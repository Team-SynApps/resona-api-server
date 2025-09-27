package com.synapps.resona.domain.repository.comment.reply;

import com.synapps.resona.domain.entity.comment.Reply;
import java.util.List;

public interface ReplyRepositoryCustom {
  List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId);
}
