package com.synapps.resona.fixture;

import static org.mockito.Mockito.mock;

import com.synapps.resona.query.dto.comment.ReplyDto;
import com.synapps.resona.query.dto.comment.request.ReplyRequest;
import com.synapps.resona.query.dto.comment.request.ReplyUpdateRequest;
import com.synapps.resona.domain.entity.comment.ContentDisplayStatus;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.query.dto.feed.SocialMemberDto;
import java.time.LocalDateTime;
import java.util.List;

public class ReplyFixture {

    public static ReplyRequest createReplyRequest(Long commentId, String content) {
        return new ReplyRequest(commentId, content);
    }

    public static ReplyDto createReplyDto(Long commentId, Long replyId, String content) {
        return ReplyDto.builder()
                .commentId(commentId)
                .replyId(replyId)
                .author(SocialMemberDto.of(1L, "test_user", "test_url"))
                .content(content)
                .status(ContentDisplayStatus.NORMAL)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static List<ReplyDto> createReplyDtoList(Long commentId) {
        return List.of(
                createReplyDto(commentId, 201L, "This is a reply."),
                createReplyDto(commentId, 202L, "This is a reply 2.")
        );
    }

    public static ReplyUpdateRequest createReplyUpdateRequest(Long replyId, String content) {
        return new ReplyUpdateRequest(replyId, content);
    }

    public static Reply createMockReply(Long replyId, String content) {
        Reply mockReply = mock(Reply.class);
        return mockReply;
    }
}
