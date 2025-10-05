package com.synapps.resona.retrieval.service;

import com.synapps.resona.retrieval.query.entity.FeedDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedDocumentUpdateService {

  private final MongoTemplate mongoTemplate;

  public void updateFeedLikeCount(Long feedId, int delta) {
    Query query = Query.query(Criteria.where("feedId").is(feedId));
    Update update = new Update().inc("like_count", delta);
    mongoTemplate.updateFirst(query, update, FeedDocument.class);
  }

  public void updateFeedCommentCount(Long feedId, int delta) {
    Query query = Query.query(Criteria.where("feedId").is(feedId));
    Update update = new Update().inc("comment_count", delta);
    mongoTemplate.updateFirst(query, update, FeedDocument.class);
  }

  // TODO: FeedDocument를 수정하는 다른 로직들도 이 서비스에서 구현
}