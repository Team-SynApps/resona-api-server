package synapps.resona.api.mysql.socialMedia.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.repository.comment.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.feed.FeedRepository;

@Transactional
@DataJpaTest
class FeedRepositoryTest {

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private EntityManager em;

  private Member member;
  private Feed feed;

  @BeforeEach
  void setUp() {
    member = createMember("test@example.com", "닉네임");
    memberRepository.save(member);

    feed = Feed.of(member, "첫 피드", "DAILY");
    feedRepository.save(feed);
  }

  @Test
  @DisplayName("특정 멤버가 작성한 모든 피드를 조회한다.")
  void findAllByMember() {
    // when
    List<Feed> result = feedRepository.findAllByMember(member);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getContent()).isEqualTo("첫 피드");
  }

  @Test
  @DisplayName("피드 ID로 멤버 정보까지 fetch join 조회한다.")
  void findWithMemberById() {
    // when
    Optional<Feed> result = feedRepository.findWithMemberById(feed.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getMember().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("피드 ID로 댓글까지 fetch join 조회한다.")
  void findWithCommentById() {
    // given
    Comment comment = Comment.of(feed, member, "댓글입니다");
    commentRepository.save(comment);
    em.flush();
    em.clear();

    // when
    Optional<Feed> result = feedRepository.findWithCommentById(feed.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getComments()).hasSize(1);
    assertThat(result.get().getComments().get(0).getContent()).isEqualTo("댓글입니다");
  }

  @Test
  @DisplayName("커서 기반으로 피드를 조회한다.")
  void findFeedsByCursor() {
    // given
    LocalDateTime cursor = LocalDateTime.now().plusSeconds(1);
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    List<Feed> result = feedRepository.findFeedsByCursor(cursor, pageable);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getContent()).isEqualTo("첫 피드");
  }


  @Test
  @DisplayName("멤버 ID와 커서 기반으로 피드를 조회한다.")
  void findFeedsByCursorAndMemberId() {
    // given
    LocalDateTime cursor = LocalDateTime.now().plusSeconds(1);
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    List<Feed> result = feedRepository.findFeedsByCursorAndMemberId(member.getId(), cursor,
        pageable);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getMember().getId()).isEqualTo(member.getId());
  }


  @Test
  @DisplayName("ID와 멤버로 존재 여부를 확인한다.")
  void existsByIdAndMember() {
    // when
    boolean exists = feedRepository.existsByIdAndMember(feed.getId(), member);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("커서보다 과거에 생성된 피드만 size만큼 조회된다.")
  void findFeedsByCursor_withMultipleFeeds() {
    // given
    for (int i = 1; i <= 5; i++) {
      Feed f = Feed.of(member, "피드 " + i, "DAILY");
      feedRepository.save(f);
      sleep(10); // createdAt 시간 차이를 위해 약간의 sleep
    }

    LocalDateTime cursor = LocalDateTime.now().plusSeconds(1);
    Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    List<Feed> result = feedRepository.findFeedsByCursor(cursor, pageable);

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt());
  }

  @Test
  @DisplayName("커서보다 과거인 피드가 없으면 빈 리스트를 반환한다.")
  void findFeedsByCursor_noFeedsBeforeCursor() {
    // given
    LocalDateTime cursor = LocalDateTime.now().minusMonths(3);
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    List<Feed> result = feedRepository.findFeedsByCursor(cursor, pageable);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("특정 멤버의 피드를 커서 기반으로 size만큼 잘라 조회한다.")
  void findFeedsByCursorAndMemberId_multipleFeeds() {
    // given
    for (int i = 0; i < 4; i++) {
      Feed f = Feed.of(member, "멤버 피드 " + i, "DAILY");
      feedRepository.save(f);
      sleep(10);
    }

    LocalDateTime cursor = LocalDateTime.now().plusSeconds(1);
    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    List<Feed> result = feedRepository.findFeedsByCursorAndMemberId(member.getId(), cursor,
        pageable);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).allMatch(f -> f.getMember().getId().equals(member.getId()));
  }


  private Member createMember(String email, String nickname) {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011112222", MBTI.INFP, "소개글", "위치");
    Profile profile = Profile.of(
        CountryCode.KR, CountryCode.KR,
        Set.of(Language.KOREAN), Set.of(Language.ENGLISH),
        nickname,nickname+"-tag", "http://img.url/" + nickname, "2000-01-01"
    );

    return Member.of(accountInfo, memberDetails, profile, email, "password", LocalDateTime.now());
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
