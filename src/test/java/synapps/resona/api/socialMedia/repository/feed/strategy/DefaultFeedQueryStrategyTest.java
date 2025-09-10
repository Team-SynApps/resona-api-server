package synapps.resona.api.socialMedia.repository.feed.strategy;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.account.AccountStatus;
import synapps.resona.api.member.entity.account.RoleType;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.socialMedia.dto.feed.condition.DefaultFeedSearchCondition;
import synapps.resona.api.socialMedia.dto.feed.FeedSortBy;
import synapps.resona.api.socialMedia.dto.feed.FeedDto;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.feed.FeedCategory;
import synapps.resona.api.socialMedia.entity.feed.Likes;
import synapps.resona.api.socialMedia.entity.restriction.Block;
import synapps.resona.api.socialMedia.entity.restriction.FeedHide;
import synapps.resona.api.socialMedia.repository.feed.dsl.FeedExpressions;
import synapps.resona.api.socialMedia.repository.feed.strategy.DefaultFeedQueryStrategy;

@DataJpaTest
@Import({TestQueryDslConfig.class, FeedExpressions.class})
class DefaultFeedQueryStrategyTest {

  @Autowired
  private EntityManager em;

  @Autowired
  private JPAQueryFactory queryFactory;

  @Autowired
  private FeedExpressions feedExpressions;

  private DefaultFeedQueryStrategy defaultFeedQueryStrategy;

  // 테스트용 데이터
  private Member viewer, writer1, writer2, blockedWriter;
  private Feed feed1, feed2, feed3, blockedFeed, hiddenFeed, countTestFeed;

  @BeforeEach
  void setUp() {
    defaultFeedQueryStrategy = new DefaultFeedQueryStrategy(queryFactory, feedExpressions);

    // 테스트 데이터 생성
    viewer = createAndPersistMember("viewer@test.com", "viewer");
    writer1 = createAndPersistMember("writer1@test.com", "writer1");
    writer2 = createAndPersistMember("writer2@test.com", "writer2");
    blockedWriter = createAndPersistMember("blocked@test.com", "blockedWriter");

    // viewer가 blockedWriter를 차단
    Block block = Block.of(viewer, blockedWriter);
    em.persist(block);

    // viewer가 숨김 처리한 피드
    hiddenFeed = createAndPersistFeed(writer1, "This is a hidden feed.", LocalDateTime.now().minusHours(1));
    FeedHide feedHide = FeedHide.of(viewer, hiddenFeed);
    em.persist(feedHide);

    // 차단된 사용자의 피드
    blockedFeed = createAndPersistFeed(blockedWriter, "This is from a blocked writer.", LocalDateTime.now().minusHours(2));

    // 카운트 테스트용 피드
    countTestFeed = createAndPersistFeed(writer1, "Feed for count test.", LocalDateTime.now().minusHours(3));
    // 좋아요 2개
    em.persist(Likes.of(viewer, countTestFeed));
    em.persist(Likes.of(writer2, countTestFeed));
    // 댓글 3개
    em.persist(Comment.of(countTestFeed, viewer, "comment 1"));
    em.persist(Comment.of(countTestFeed, writer2, "comment 2"));
    em.persist(Comment.of(countTestFeed, blockedWriter, "comment 3"));

    // 일반 피드들 (최신순)
    feed3 = createAndPersistFeed(writer2, "Latest Feed", LocalDateTime.now().minusHours(4));
    feed2 = createAndPersistFeed(writer1, "Old Feed", LocalDateTime.now().minusHours(5));
    feed1 = createAndPersistFeed(writer2, "Oldest Feed", LocalDateTime.now().minusHours(6));

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("최신순으로 피드를 정상적으로 조회하며, 차단/숨김된 피드는 제외된다")
  void findFeeds_LatestSort_Success() {
    // given
    DefaultFeedSearchCondition condition = DefaultFeedSearchCondition.of(
        viewer.getId(),
        LocalDateTime.now(),
        FeedSortBy.LATEST
    );

    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = defaultFeedQueryStrategy.findFeeds(condition, condition.getCursor(), pageable, viewer.getId());

    // then
    // viewer에게 보여야 할 피드는 countTestFeed, feed3, feed2, feed1 총 4개
    assertThat(result).hasSize(4);
    // 최신순 정렬 확인
    assertThat(result).isSortedAccordingTo((f1, f2) -> f2.getCreatedAt().compareTo(f1.getCreatedAt()));
    // 첫번째 피드는 가장 최신인 countTestFeed
    assertThat(result.get(0).getFeedId()).isEqualTo(countTestFeed.getId());
  }

  @Test
  @DisplayName("차단한 사용자의 피드는 조회되지 않는다")
  void findFeeds_Excludes_Blocked_Users_Feed() {
    // given
    DefaultFeedSearchCondition condition = DefaultFeedSearchCondition.of(
        viewer.getId(),
        null,
        null
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = defaultFeedQueryStrategy.findFeeds(condition, null, pageable, viewer.getId());

    // then
    List<Long> resultFeedIds = result.stream().map(FeedDto::getFeedId).toList();
    assertThat(resultFeedIds).doesNotContain(blockedFeed.getId());
  }

  @Test
  @DisplayName("숨김 처리한 피드는 조회되지 않는다")
  void findFeeds_Excludes_Hidden_Feed() {
    // given
    DefaultFeedSearchCondition condition = DefaultFeedSearchCondition.of(
        viewer.getId(),
        null,
        null
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = defaultFeedQueryStrategy.findFeeds(condition, null, pageable, viewer.getId());

    // then
    List<Long> resultFeedIds = result.stream().map(FeedDto::getFeedId).toList();
    assertThat(resultFeedIds).doesNotContain(hiddenFeed.getId());
  }

  @Test
  @DisplayName("좋아요와 댓글 수가 정확하게 카운트된다")
  void findFeeds_Counts_Likes_And_Comments_Correctly() {
    // given
    DefaultFeedSearchCondition condition = DefaultFeedSearchCondition.of(
        viewer.getId(),
        null,
        FeedSortBy.LATEST
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = defaultFeedQueryStrategy.findFeeds(condition, LocalDateTime.now(), pageable, viewer.getId());

    // then
    FeedDto resultDto = result.stream()
        .filter(dto -> dto.getFeedId().equals(countTestFeed.getId()))
        .findFirst()
        .orElse(null);

    assertThat(resultDto).isNotNull();
    assertThat(resultDto.getLikeCount()).isEqualTo(2);
    assertThat(resultDto.getCommentCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("페이지네이션이 정확하게 동작한다")
  void findFeeds_Pagination_Works_Correctly() {
    // given
    // viewer에게 보이는 피드는 총 4개 (countTestFeed, feed3, feed2, feed1)
    DefaultFeedSearchCondition condition = DefaultFeedSearchCondition.of(
        viewer.getId(),
        null,
        FeedSortBy.LATEST
    );

    // when: 첫 페이지 (size: 3)
    Pageable page1 = PageRequest.of(0, 3);
    List<FeedDto> result1 = defaultFeedQueryStrategy.findFeeds(condition, LocalDateTime.now(), page1, viewer.getId());

    // then: 3개 조회
    assertThat(result1).hasSize(3);
    assertThat(result1.get(0).getFeedId()).isEqualTo(countTestFeed.getId());

    // when: 두 번째 페이지 (size: 3)
    LocalDateTime cursor = result1.get(2).getCreatedAt(); // 마지막 피드의 시간을 커서로 사용
    Pageable page2 = PageRequest.of(0, 3); // 페이지 번호는 0으로 유지, 커서가 오프셋 역할을 함
    DefaultFeedSearchCondition condition2 = DefaultFeedSearchCondition.of(
        viewer.getId(),
        cursor,
        FeedSortBy.LATEST
    );
    List<FeedDto> result2 = defaultFeedQueryStrategy.findFeeds(condition2, condition2.getCursor(), page2, viewer.getId());

    // then: 나머지 1개 조회
    assertThat(result2).hasSize(1);
    assertThat(result2.get(0).getFeedId()).isEqualTo(feed1.getId());
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

  private Feed createAndPersistFeed(Member member, String content, LocalDateTime createdAt) {
    Feed feed = Feed.of(member, content, FeedCategory.DAILY.name(), "ko");
    em.persist(feed);
    // BaseEntity의 createdAt을 강제로 설정하기 위해 JPQL 사용
    em.createQuery("UPDATE Feed f SET f.createdAt = :createdAt WHERE f.id = :id")
        .setParameter("createdAt", createdAt)
        .setParameter("id", feed.getId())
        .executeUpdate();
    return feed;
  }
}