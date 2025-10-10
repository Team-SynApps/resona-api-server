
package com.synapps.resona.retrieval.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.synapps.resona.retrieval.query.entity.FeedDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class FeedDocumentUpdateServiceTest {

    @InjectMocks
    private FeedDocumentUpdateService feedDocumentUpdateService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("피드 좋아요 수 업데이트")
    void updateFeedLikeCount() {
        // given
        Long feedId = 1L;
        int delta = 1;

        // when
        feedDocumentUpdateService.updateFeedLikeCount(feedId, delta);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(FeedDocument.class));
    }

    @Test
    @DisplayName("피드 댓글 수 업데이트")
    void updateFeedCommentCount() {
        // given
        Long feedId = 1L;
        int delta = 1;

        // when
        feedDocumentUpdateService.updateFeedCommentCount(feedId, delta);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(FeedDocument.class));
    }
}
