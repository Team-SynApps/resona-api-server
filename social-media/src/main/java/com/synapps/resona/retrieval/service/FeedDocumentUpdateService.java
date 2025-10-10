package com.synapps.resona.retrieval.service;

import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import java.util.List;
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

  public void updateOrAddTranslation(Long feedId, Translation translation) {
    Query query = Query.query(Criteria.where("feedId").is(feedId));

    Update pullUpdate = new Update().pull("translations",
        Query.query(Criteria.where("code").is(translation.languageCode()))
    );
    mongoTemplate.updateFirst(query, pullUpdate, FeedDocument.class);

    Update pushUpdate = new Update().push("translations", translation);
    mongoTemplate.updateFirst(query, pushUpdate, FeedDocument.class);
  }

  public void addTranslations(Long feedId, List<Translation> newTranslations) {
    if (newTranslations == null || newTranslations.isEmpty()) {
      return;
    }

    Query query = Query.query(Criteria.where("feedId").is(feedId));

    List<String> languageCodes = newTranslations.stream()
        .map(Translation::languageCode)
        .toList();

    Update pullUpdate = new Update().pull("translations",
        Query.query(Criteria.where("code").in(languageCodes))
    );
    mongoTemplate.updateFirst(query, pullUpdate, FeedDocument.class);

    Update pushUpdate = new Update().push("translations").each(newTranslations.toArray());
    mongoTemplate.updateFirst(query, pushUpdate, FeedDocument.class);
  }

  public void updateContent(Long feedId, String newContent) {
    Query query = Query.query(Criteria.where("feedId").is(feedId));
    Update update = new Update().set("content", newContent);
    mongoTemplate.updateFirst(query, update, FeedDocument.class);
  }

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