package synapps.resona.api.fixture;

import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.dto.comment.CommentDto;
import synapps.resona.api.socialMedia.dto.comment.request.CommentRequest;
import synapps.resona.api.socialMedia.dto.comment.request.CommentUpdateRequest;
import synapps.resona.api.socialMedia.dto.feed.SocialMemberDto;
import synapps.resona.api.socialMedia.entity.comment.Comment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import synapps.resona.api.socialMedia.entity.comment.CommentLikes;
import synapps.resona.api.socialMedia.entity.feed.Feed;

import static org.mockito.Mockito.mock;

public class CommentFixture {

    public static Comment createComment(Feed feed, Member member, String content) {
        return Comment.of(feed, member, content);
    }

    public static CommentLikes createCommentLike(Member member, Comment comment) {
        return CommentLikes.of(member, comment);
    }

    public static CommentRequest createCommentRequest(Long feedId, String content) {
        return new CommentRequest(feedId, content);
    }

    public static CommentUpdateRequest createCommentUpdateRequest(Long commentId, String content) {
        return new CommentUpdateRequest(commentId, content);
    }

    public static CommentDto createCommentDto(Long commentId, String content) {
        return CommentDto.builder()
                .commentId(commentId)
                .author(SocialMemberDto.of(1L, "test_user", "test_url"))
                .content(content)
                .status(synapps.resona.api.socialMedia.entity.comment.ContentDisplayStatus.NORMAL)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .replies(Collections.emptyList())
                .build();
    }

    public static List<CommentDto> createCommentDtoList() {
        return List.of(
                createCommentDto(101L, "First comment"),
                createCommentDto(102L, "Second comment")
        );
    }

    public static Comment createMockComment(Long commentId, String content) {
        return mock(Comment.class);
    }
}
