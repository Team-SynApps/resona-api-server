package com.synapps.resona.fixture;

import static org.mockito.Mockito.mock;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.request.CommentRequest;
import com.synapps.resona.comment.dto.request.CommentUpdateRequest;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.entity.ContentDisplayStatus;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.dto.SocialMemberDto;
import com.synapps.resona.likes.entity.CommentLikes;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
                .status(ContentDisplayStatus.NORMAL)
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
