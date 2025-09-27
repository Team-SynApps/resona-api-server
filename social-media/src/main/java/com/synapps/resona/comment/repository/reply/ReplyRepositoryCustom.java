package com.synapps.resona.comment.repository.reply;

import com.synapps.resona.comment.entity.Reply;
import java.util.List;

public interface ReplyRepositoryCustom {
  List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId);
}
