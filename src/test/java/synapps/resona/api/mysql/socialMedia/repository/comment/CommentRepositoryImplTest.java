package synapps.resona.api.mysql.socialMedia.repository.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.FeedCategory;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Block;
import synapps.resona.api.mysql.socialMedia.entity.restriction.CommentHide;
import synapps.resona.api.oauth.entity.ProviderType;


@DataJpaTest
@Import(TestQueryDslConfig.class)
class CommentRepositoryImplTest {

  @Autowired
  private EntityManager em;

  @Autowired
  private JPAQueryFactory queryFactory;

  private CommentRepositoryImpl commentRepository;

  // 테스트용 데이터
  private Member viewer, writer, blockedWriter;
  private Feed feed;
  private Comment normalComment, hiddenComment, blockedComment, otherFeedComment;


  @BeforeEach
  void setUp() {
    commentRepository = new CommentRepositoryImpl(queryFactory);

    // 테스트 데이터 생성
    viewer = createAndPersistMember("viewer@test.com", "viewer");
    writer = createAndPersistMember("writer@test.com", "writer");
    blockedWriter = createAndPersistMember("blocked@test.com", "blocked");

    // viewer가 blockedWriter를 차단
    em.persist(Block.of(viewer, blockedWriter));

    // 테스트용 피드 생성
    feed = createAndPersistFeed(writer);
    Feed anotherFeed = createAndPersistFeed(writer);

    // 정상적으로 보여야 할 댓글
    normalComment = createAndPersistComment(feed, writer, "This is a normal comment.");

    // viewer가 숨김 처리한 댓글
    hiddenComment = createAndPersistComment(feed, writer, "This is a hidden comment.");
    em.persist(CommentHide.of(viewer, hiddenComment));

    // 차단된 사용자가 작성한 댓글
    blockedComment = createAndPersistComment(feed, blockedWriter, "This is from a blocked writer.");

    // 다른 피드에 달린 댓글 (조회되면 안 됨)
    otherFeedComment = createAndPersistComment(anotherFeed, writer, "This is on another feed.");

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("피드 ID로 댓글 조회 시, 차단/숨김된 댓글을 제외하고 정상적으로 조회한다")
  void findAllCommentsByFeedIdWithReplies_Success() {
    // when
    List<Comment> result = commentRepository.findAllCommentsByFeedIdWithReplies(viewer.getId(), feed.getId());

    // then
    // 최종적으로 normalComment 1개만 조회되어야 함
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(normalComment.getId());
    assertThat(result.get(0).getContent()).isEqualTo("This is a normal comment.");
  }

  @Test
  @DisplayName("조회 결과에 차단한 사용자의 댓글이 포함되지 않는다")
  void findAllCommentsByFeedIdWithReplies_Excludes_Blocked_User_Comments() {
    // when
    List<Comment> result = commentRepository.findAllCommentsByFeedIdWithReplies(viewer.getId(), feed.getId());

    // then
    List<Long> commentIds = result.stream().map(Comment::getId).toList();
    assertThat(commentIds).doesNotContain(blockedComment.getId());
  }

  @Test
  @DisplayName("조회 결과에 숨김 처리한 댓글이 포함되지 않는다")
  void findAllCommentsByFeedIdWithReplies_Excludes_Hidden_Comments() {
    // when
    List<Comment> result = commentRepository.findAllCommentsByFeedIdWithReplies(viewer.getId(), feed.getId());

    // then
    List<Long> commentIds = result.stream().map(Comment::getId).toList();
    assertThat(commentIds).doesNotContain(hiddenComment.getId());
  }

  @Test
  @DisplayName("조회 결과에 다른 피드의 댓글이 포함되지 않는다")
  void findAllCommentsByFeedIdWithReplies_Excludes_Other_Feed_Comments() {
    // when
    List<Comment> result = commentRepository.findAllCommentsByFeedIdWithReplies(viewer.getId(), feed.getId());

    // then
    List<Long> commentIds = result.stream().map(Comment::getId).toList();
    assertThat(commentIds).doesNotContain(otherFeedComment.getId());
  }

  private Member createAndPersistMember(String email, String nickname) {
    AccountInfo accountInfo = AccountInfo.of(RoleType.USER, AccountStatus.ACTIVE);
    MemberDetails memberDetails = MemberDetails.empty();
    Profile profile = Profile.of(CountryCode.KR, CountryCode.KR, Set.of(Language.KOREAN), Collections.emptySet(), nickname, "tag_" + nickname, "", "2000-01-01");
    Member member = Member.of(accountInfo, memberDetails, profile, email, "password", LocalDateTime.now());

    em.persist(accountInfo);
    em.persist(memberDetails);
    em.persist(profile);
    em.persist(member);
    return member;
  }

  private Feed createAndPersistFeed(Member member) {
    Feed feed = Feed.of(member, "Test Feed Content", FeedCategory.DAILY.name());
    em.persist(feed);
    return feed;
  }

  private Comment createAndPersistComment(Feed feed, Member member, String content) {
    Comment comment = Comment.of(feed, member, content);
    em.persist(comment);
    return comment;
  }
}