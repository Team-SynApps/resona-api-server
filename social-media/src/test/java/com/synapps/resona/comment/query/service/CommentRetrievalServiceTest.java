
package com.synapps.resona.comment.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.mock;
import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.entity.Language;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.repository.CommentDocumentRepository;
import com.synapps.resona.query.member.service.MemberStateService;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CommentRetrievalServiceTest {

    @InjectMocks
    private CommentRetrievalService commentRetrievalService;

    @Mock
    private CommentDocumentRepository commentDocumentRepository;

    @Mock
    private MemberStateService memberStateService;

    @Mock
    private CommentStatusCalculator statusCalculator;

    @Test
    @DisplayName("피드에 대한 댓글 목록을 성공적으로 조회한다.")
    void getCommentsForFeed_Success() {
        // given
        Long feedId = 1L;
        Long viewerId = 2L;
        PageRequest pageable = PageRequest.of(0, 10);
        CommentDocument commentDocument = mock(CommentDocument.class);
        Page<CommentDocument> commentPage = new PageImpl<>(Collections.singletonList(commentDocument));

        when(memberStateService.getHiddenCommentIds(viewerId)).thenReturn(Collections.emptySet());
        when(memberStateService.getHiddenReplyIds(viewerId)).thenReturn(Collections.emptySet());
        when(memberStateService.getBlockedMemberIds(viewerId)).thenReturn(Collections.emptySet());
        when(commentDocumentRepository.findByFeedIdOrderByCreatedAtDesc(feedId, pageable)).thenReturn(commentPage);
        when(commentDocument.getTranslations()).thenReturn(Collections.emptyList());
        when(commentDocument.getLanguage()).thenReturn(Language.ko);
        when(statusCalculator.determineCommentStatus(any(), any())).thenReturn(CommentDisplayStatus.NORMAL);
        when(statusCalculator.getDisplayContent(any(), any())).thenReturn("Test Comment");

        // when
        Page<CommentDto> result = commentRetrievalService.getCommentsForFeed(feedId, viewerId, Language.ko, pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Test Comment");
    }
}
