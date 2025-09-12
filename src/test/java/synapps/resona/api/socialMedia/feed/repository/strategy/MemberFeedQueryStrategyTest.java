package synapps.resona.api.socialMedia.feed.repository.strategy;

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
import synapps.resona.api.socialMedia.feed.dto.condition.MemberFeedSearchCondition;
import synapps.resona.api.socialMedia.feed.dto.FeedSortBy;
import synapps.resona.api.socialMedia.feed.dto.FeedDto;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.feed.entity.FeedCategory;
import synapps.resona.api.socialMedia.likes.entity.FeedLikes;
import synapps.resona.api.socialMedia.feed.repository.dsl.FeedExpressions;

@DataJpaTest
@Import({TestQueryDslConfig.class, FeedExpressions.class})
class MemberFeedQueryStrategyTest {

  @Autowired
  private EntityManager em;

  @Autowired
  private JPAQueryFactory queryFactory;

  @Autowired
  private FeedExpressions feedExpressions;

  private MemberFeedQueryStrategy memberFeedQueryStrategy;

  // 테스트용 데이터
  private Member writer1, writer2, viewer;
  private Feed writer1Feed1, writer1Feed2, writer1Feed3_withCount;
  private Feed writer2Feed1;

  @BeforeEach
  void setUp() {
    memberFeedQueryStrategy = new MemberFeedQueryStrategy(queryFactory, feedExpressions);

    // 테스트 데이터 생성
    writer1 = createAndPersistMember("writer1@test.com", "writer1");
    writer2 = createAndPersistMember("writer2@test.com", "writer2");
    viewer = createAndPersistMember("viewer@test.com", "viewer"); // 좋아요, 댓글용

    // writer1의 피드 3개 생성
    writer1Feed3_withCount = createAndPersistFeed(writer1, "writer1's Latest Feed", LocalDateTime.now().minusHours(1));
    writer1Feed2 = createAndPersistFeed(writer1, "writer1's Middle Feed", LocalDateTime.now().minusHours(2));
    writer1Feed1 = createAndPersistFeed(writer1, "writer1's Oldest Feed", LocalDateTime.now().minusHours(3));

    // writer2의 피드 1개 생성
    writer2Feed1 = createAndPersistFeed(writer2, "writer2's Feed", LocalDateTime.now());

    // 카운트 테스트를 위해 writer1Feed3_withCount에 좋아요/댓글 추가
    em.persist(FeedLikes.of(viewer, writer1Feed3_withCount));
    em.persist(FeedLikes.of(writer2, writer1Feed3_withCount));
    em.persist(Comment.of(writer1Feed3_withCount, viewer, "comment 1"));

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("특정 사용자의 피드만 최신순으로 정확히 조회한다")
  void findFeeds_ForSpecificMember_SortedByLatest() {
    // given
    MemberFeedSearchCondition condition = MemberFeedSearchCondition.of(
        viewer.getId(),
        writer1.getId(), // writer1의 피드만 조회
        null,
        FeedSortBy.LATEST
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = memberFeedQueryStrategy.findFeeds(condition, null, pageable, viewer.getId());

    // then
    assertThat(result).hasSize(3); // writer1의 피드 3개만 조회되어야 함
    assertThat(result).allMatch(dto -> dto.getAuthor().getMemberId().equals(writer1.getId())); // 모든 결과가 writer1의 피드인지 확인
    assertThat(result.get(0).getFeedId()).isEqualTo(writer1Feed3_withCount.getId()); // 최신순 정렬 확인
  }

  @Test
  @DisplayName("특정 사용자 피드의 좋아요와 댓글 수가 정확하게 카운트된다")
  void findFeeds_Counts_Likes_And_Comments_Correctly() {
    // given
    MemberFeedSearchCondition condition = MemberFeedSearchCondition.of(
        viewer.getId(),
        writer1.getId(),
        null,
        FeedSortBy.LATEST
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = memberFeedQueryStrategy.findFeeds(condition, null, pageable, viewer.getId());

    // then
    FeedDto resultDto = result.stream()
        .filter(dto -> dto.getFeedId().equals(writer1Feed3_withCount.getId()))
        .findFirst()
        .orElse(null);

    assertThat(resultDto).isNotNull();
    assertThat(resultDto.getLikeCount()).isEqualTo(2);
    assertThat(resultDto.getCommentCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("특정 사용자 피드 조회 시 페이지네이션이 정확하게 동작한다")
  void findFeeds_ForSpecificMember_Pagination_Works_Correctly() {
    // given
    // writer1의 피드는 총 3개
    MemberFeedSearchCondition condition = MemberFeedSearchCondition.of(
        viewer.getId(),
        writer1.getId(),
        null,
        FeedSortBy.LATEST
    );

    // when: 첫 페이지 (size: 2)
    Pageable page1 = PageRequest.of(0, 2);
    List<FeedDto> result1 = memberFeedQueryStrategy.findFeeds(condition, null, page1, viewer.getId());

    // then: 2개 조회
    assertThat(result1).hasSize(2);
    assertThat(result1.get(0).getFeedId()).isEqualTo(writer1Feed3_withCount.getId());

    // when: 두 번째 페이지 (size: 2)
    LocalDateTime cursor = result1.get(1).getCreatedAt();
    Pageable page2 = PageRequest.of(0, 2);
    MemberFeedSearchCondition condition2 = MemberFeedSearchCondition.of(
        viewer.getId(),
        writer1.getId(),
        cursor,
        FeedSortBy.LATEST
    );
    List<FeedDto> result2 = memberFeedQueryStrategy.findFeeds(condition2, cursor, page2, viewer.getId());

    // then: 나머지 1개 조회
    assertThat(result2).hasSize(1);
    assertThat(result2.get(0).getFeedId()).isEqualTo(writer1Feed1.getId());
  }

  @Test
  @DisplayName("피드가 없는 사용자를 조회하면 빈 리스트를 반환한다")
  void findFeeds_ForMemberWithNoFeeds_ReturnsEmptyList() {
    // given
    Member memberWithNoFeeds = createAndPersistMember("nofeeds@test.com", "noFeeds");
    em.flush();
    em.clear();

    MemberFeedSearchCondition condition = MemberFeedSearchCondition.of(
        viewer.getId(),
        memberWithNoFeeds.getId(), // 피드가 없는 사용자
        null,
        FeedSortBy.LATEST
    );
    Pageable pageable = PageRequest.of(0, 10);

    // when
    List<FeedDto> result = memberFeedQueryStrategy.findFeeds(condition, null, pageable, viewer.getId());

    // then
    assertThat(result).isEmpty();
  }


  // === Helper Methods ===
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
    em.createQuery("UPDATE Feed f SET f.createdAt = :createdAt WHERE f.id = :id")
        .setParameter("createdAt", createdAt)
        .setParameter("id", feed.getId())
        .executeUpdate();
    return feed;
  }
}