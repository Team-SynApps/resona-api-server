
package com.synapps.resona.comment.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.query.dto.CommentViewerContext;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.common.entity.Author;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentStatusCalculatorTest {

    private CommentStatusCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CommentStatusCalculator();
    }

    @Test
    @DisplayName("일반 댓글의 상태를 정확하게 결정한다.")
    void determineCommentStatus_Normal() {
        // given
        CommentDocument comment = mock(CommentDocument.class);
        Author author = mock(Author.class);
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getAuthor()).thenReturn(author);
        when(author.getMemberId()).thenReturn(2L);
        when(comment.getCommentId()).thenReturn(3L);
        CommentViewerContext context = new CommentViewerContext(1L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        CommentDisplayStatus status = calculator.determineCommentStatus(comment, context);

        // then
        assertThat(status).isEqualTo(CommentDisplayStatus.NORMAL);
    }

    @Test
    @DisplayName("삭제된 댓글의 상태를 정확하게 결정한다.")
    void determineCommentStatus_Deleted() {
        // given
        CommentDocument comment = mock(CommentDocument.class);
        when(comment.isDeleted()).thenReturn(true);
        CommentViewerContext context = new CommentViewerContext(1L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        CommentDisplayStatus status = calculator.determineCommentStatus(comment, context);

        // then
        assertThat(status).isEqualTo(CommentDisplayStatus.DELETED);
    }

    @Test
    @DisplayName("차단된 사용자의 댓글 상태를 정확하게 결정한다.")
    void determineCommentStatus_Blocked() {
        // given
        CommentDocument comment = mock(CommentDocument.class);
        Author author = mock(Author.class);
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getAuthor()).thenReturn(author);
        when(author.getMemberId()).thenReturn(2L);
        CommentViewerContext context = new CommentViewerContext(1L, Collections.emptySet(), Collections.emptySet(), Set.of(2L), Collections.emptySet(), Collections.emptySet());

        // when
        CommentDisplayStatus status = calculator.determineCommentStatus(comment, context);

        // then
        assertThat(status).isEqualTo(CommentDisplayStatus.BLOCKED);
    }

    @Test
    @DisplayName("숨겨진 댓글의 상태를 정확하게 결정한다.")
    void determineCommentStatus_Hidden() {
        // given
        CommentDocument comment = mock(CommentDocument.class);
        Author author = mock(Author.class);
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getAuthor()).thenReturn(author);
        when(author.getMemberId()).thenReturn(2L);
        when(comment.getCommentId()).thenReturn(3L);
        CommentViewerContext context = new CommentViewerContext(1L, Set.of(3L), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        CommentDisplayStatus status = calculator.determineCommentStatus(comment, context);

        // then
        assertThat(status).isEqualTo(CommentDisplayStatus.HIDDEN);
    }

    @Test
    @DisplayName("일반 답글의 상태를 정확하게 결정한다.")
    void determineReplyStatus_Normal() {
        // given
        ReplyEmbed reply = mock(ReplyEmbed.class);
        Author author = mock(Author.class);
        when(reply.isDeleted()).thenReturn(false);
        when(reply.getAuthor()).thenReturn(author);
        when(author.getMemberId()).thenReturn(2L);
        when(reply.getReplyId()).thenReturn(4L);
        CommentViewerContext context = new CommentViewerContext(1L, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        // when
        CommentDisplayStatus status = calculator.determineReplyStatus(reply, context);

        // then
        assertThat(status).isEqualTo(CommentDisplayStatus.NORMAL);
    }

    @Test
    @DisplayName("NORMAL 상태일 때 원본 내용을 반환한다.")
    void getDisplayContent_Normal() {
        // given
        String content = "Original Content";

        // when
        String displayContent = calculator.getDisplayContent(content, CommentDisplayStatus.NORMAL);

        // then
        assertThat(displayContent).isEqualTo(content);
    }

    @Test
    @DisplayName("NORMAL이 아닌 상태일 때 상태 설명을 반환한다.")
    void getDisplayContent_NotNormal() {
        // given
        String content = "Original Content";

        // when
        String displayContent = calculator.getDisplayContent(content, CommentDisplayStatus.DELETED);

        // then
        assertThat(displayContent).isEqualTo(CommentDisplayStatus.DELETED.getDescription());
    }
}
