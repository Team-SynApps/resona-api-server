package com.synapps.resona.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.request.CommentRequest;
import com.synapps.resona.comment.dto.request.CommentUpdateRequest;
import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.comment.repository.comment.CommentRepository;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.fixture.CommentFixture;
import com.synapps.resona.fixture.FeedFixture;
import com.synapps.resona.fixture.FeedMemberFixture;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.repository.member.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@ExtendWith(MockitoExtension.class)
@Disabled
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private FeedRepository feedRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private MemberRepository memberRepository;

  private Member member;
  private Feed feed;

  @BeforeEach
  void setUp() {
    member = FeedMemberFixture.createMember("user@example.com", "testUser");
    member.encodePassword("securePass123!");

    feed = FeedFixture.createFeed(member, "테스트 피드입니다.");

    setAuthentication(member.getEmail());
  }

  private void setAuthentication(String email) {
    User userPrincipal = new User(email, "", java.util.Collections.emptyList());
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  @DisplayName("댓글을 등록한다.")
  void registerComment() {
    // given
    CommentRequest request = CommentFixture.createCommentRequest(feed.getId(), "댓글 내용입니다.");
    when(feedRepository.findById(feed.getId())).thenReturn(Optional.of(feed));
    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
      Comment comment = invocation.getArgument(0);
      return comment;
    });

    // when
    CommentDto response = commentService.register(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getContent()).isEqualTo("댓글 내용입니다.");
  }

  @Test
  @DisplayName("댓글을 수정한다.")
  void updateComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "기존 댓글");
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
      Comment savedComment = invocation.getArgument(0);
      return savedComment;
    });
    CommentUpdateRequest request = CommentFixture.createCommentUpdateRequest(comment.getId(), "수정된 댓글");

    // when
    CommentDto response = commentService.edit(request);

    // then
    assertThat(response.getCommentId()).isEqualTo(comment.getId());
    assertThat(response.getContent()).isEqualTo("수정된 댓글");
  }

  @Test
  @DisplayName("댓글을 단일 조회한다.")
  void readComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "단일 댓글");
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

    // when
    CommentDto response = commentService.getComment(member.getId(), comment.getId());

    // then
    assertThat(response.getCommentId()).isEqualTo(comment.getId());
    assertThat(response.getContent()).isEqualTo("단일 댓글");
  }

//  @Test
//  @DisplayName("피드에 등록된 댓글 목록을 조회한다.")
//  void readCommentsByFeedId() {
//    // given
//    Comment comment1 = CommentFixture.createComment(feed, member, "댓글1");
//    Comment comment2 = CommentFixture.createComment(feed, member, "댓글2");
//    when(feedRepository.findById(feed.getId())).thenReturn(Optional.of(feed));
//    when(commentRepository.findAllByFeedAndIsDeletedFalse(feed)).thenReturn(List.of(comment1, comment2));
//
//    // when
//    List<CommentDto> responseList = commentService.getCommentsByFeedId(1L, feed.getId());
//
//    // then
//    assertThat(responseList).hasSize(2);
//    assertThat(responseList).extracting("content").containsExactlyInAnyOrder("댓글1", "댓글2");
//  }

  @Test
  @DisplayName("댓글을 삭제한다 (soft delete).")
  void deleteComment() {
    // given
    Comment comment = CommentFixture.createComment(feed, member, "삭제될 댓글");
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
      Comment savedComment = invocation.getArgument(0);
      return savedComment;
    });

    // when
    commentService.deleteComment(comment.getId());
    // then
    assertThat(comment.isDeleted()).isTrue();
  }
}

