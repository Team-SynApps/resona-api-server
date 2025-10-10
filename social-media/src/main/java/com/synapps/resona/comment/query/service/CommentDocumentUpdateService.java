package com.synapps.resona.comment.query.service;

import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.common.entity.Translation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentDocumentUpdateService {
  private final MongoTemplate mongoTemplate;

  public void addReplyToComment(Long commentId, ReplyEmbed replyEmbed) {
    Query query = Query.query(Criteria.where("commentId").is(commentId));

    Update update = new Update()
        .inc("reply_count", 1)
        .push("replies").value(replyEmbed);

    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }

  public void softDeleteReplyInComment(Long parentCommentId, Long replyId) {
    Query query = Query.query(Criteria.where("commentId").is(parentCommentId));

    Update update = new Update()
        .set("replies.$[elem].isDeleted", true)
        .inc("reply_count", -1);

    update.filterArray(Criteria.where("elem.replyId").is(replyId));

    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }

  public void updateCommentLikeCount(Long commentId, int delta) {
    Query query = Query.query(Criteria.where("commentId").is(commentId));
    Update update = new Update().inc("like_count", delta);
    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }

  public void updateReplyLikeCount(Long parentCommentId, Long replyId, int delta) {
    Query query = Query.query(Criteria.where("commentId").is(parentCommentId));
    Update update = new Update()
        .inc("replies.$[elem].likeCount", delta)
        .filterArray(Criteria.where("elem.replyId").is(replyId));
    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }

  public void addTranslationToComment(Long commentId, Translation translation) {
    Query query = Query.query(Criteria.where("commentId").is(commentId));
    Update update = new Update().push("translations", translation);
    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }

  public void addTranslationToReply(Long parentCommentId, Long replyId, Translation translation) {
    Query query = Query.query(Criteria.where("commentId").is(parentCommentId));

    Update update = new Update()
        .push("replies.$[replyElem].translations").value(translation);

    update.filterArray(Criteria.where("replyElem.replyId").is(replyId));

    mongoTemplate.updateFirst(query, update, CommentDocument.class);
  }
}
