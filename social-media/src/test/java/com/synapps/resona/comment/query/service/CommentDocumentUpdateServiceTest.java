
package com.synapps.resona.comment.query.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
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
class CommentDocumentUpdateServiceTest {

    @InjectMocks
    private CommentDocumentUpdateService commentDocumentUpdateService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("댓글에 답글 추가")
    void addReplyToComment() {
        // given
        Long commentId = 1L;
        ReplyEmbed replyEmbed = ReplyEmbed.of(1L, null, null, "test", null);

        // when
        commentDocumentUpdateService.addReplyToComment(commentId, replyEmbed);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(CommentDocument.class));
    }

    @Test
    @DisplayName("댓글의 답글 소프트 삭제")
    void softDeleteReplyInComment() {
        // given
        Long parentCommentId = 1L;
        Long replyId = 2L;

        // when
        commentDocumentUpdateService.softDeleteReplyInComment(parentCommentId, replyId);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(CommentDocument.class));
    }

    @Test
    @DisplayName("댓글 좋아요 수 업데이트")
    void updateCommentLikeCount() {
        // given
        Long commentId = 1L;
        int delta = 1;

        // when
        commentDocumentUpdateService.updateCommentLikeCount(commentId, delta);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(CommentDocument.class));
    }

    @Test
    @DisplayName("답글 좋아요 수 업데이트")
    void updateReplyLikeCount() {
        // given
        Long parentCommentId = 1L;
        Long replyId = 2L;
        int delta = 1;

        // when
        commentDocumentUpdateService.updateReplyLikeCount(parentCommentId, replyId, delta);

        // then
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(CommentDocument.class));
    }
}
