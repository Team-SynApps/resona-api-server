package synapps.resona.api.fixture;

import synapps.resona.api.socialMedia.comment.entity.ContentDisplayStatus;
import synapps.resona.api.socialMedia.feed.dto.SocialMemberDto;
import synapps.resona.api.socialMedia.comment.dto.ReplyDto;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyRequest;
import synapps.resona.api.socialMedia.comment.dto.request.ReplyUpdateRequest;
import synapps.resona.api.socialMedia.comment.entity.Reply;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;

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
