package synapps.resona.api.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.fixture.MemberFixture;
import synapps.resona.api.member.entity.member.Follow;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.FollowRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.feed.repository.dsl.FeedExpressions;

@Transactional
@DataJpaTest
@Import({TestQueryDslConfig.class, FeedExpressions.class})
class FollowRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private FollowRepository followRepository;

  private Member follower;
  private Member following;

  @BeforeEach
  void setUp() {
    follower = MemberFixture.createCustomMember("follower@example.com", "followerNick");
    following = MemberFixture.createCustomMember("following@example.com", "followingNick");

    memberRepository.saveAll(List.of(follower, following));

    Follow follow = Follow.of(follower, following);
    followRepository.save(follow);
  }


  @Test
  @DisplayName("멤버가 팔로우한 사람들의 프로필을 fetch join으로 조회한다.")
  void findFollowingsByFollowerId() {
    // when
    List<Follow> followings = followRepository.findFollowingsByFollowerId(follower.getId());

    // then
    assertThat(followings).hasSize(1);
    Follow result = followings.get(0);
    assertThat(result.getFollowing().getEmail()).isEqualTo("following@example.com");
    assertThat(result.getFollowing().getProfile().getNickname()).isEqualTo("followingNick");
  }

  @Test
  @DisplayName("멤버를 팔로우한 사람들의 프로필을 fetch join으로 조회한다.")
  void findFollowersByFollowingId() {
    // when
    List<Follow> followers = followRepository.findFollowersByFollowingId(following.getId());

    // then
    assertThat(followers).hasSize(1);
    Follow result = followers.get(0);
    assertThat(result.getFollower().getEmail()).isEqualTo("follower@example.com");
    assertThat(result.getFollower().getProfile().getNickname()).isEqualTo("followerNick");
  }

  @Test
  @DisplayName("한 사람이 여러 명을 팔로우했을 때, 모든 following의 프로필을 fetch 한다")
  void findFollowingsByFollowerId_multiple() {
    // given
    Member follower = MemberFixture.createCustomMember("followerMany@example.com", "followerNick");
    Member another = MemberFixture.createCustomMember("another@example.com", "otherNick");
    Member another2 = MemberFixture.createCustomMember("anothertwo@example.com", "anotherNick");
    memberRepository.saveAll(List.of(follower, another, another2));

    followRepository.save(Follow.of(follower, another));
    followRepository.save(Follow.of(follower, another2));

    // when
    List<Follow> followings = followRepository.findFollowingsByFollowerId(follower.getId());

    // then
    assertThat(followings).hasSize(2);
    assertThat(followings).extracting(f -> f.getFollowing().getProfile().getNickname())
        .containsExactlyInAnyOrder("otherNick", "anotherNick");
  }

  @Test
  @DisplayName("여러 사람이 같은 사람을 팔로우했을 때, 모든 follower의 프로필을 fetch 한다")
  void findFollowersByFollowingId_multiple() {
    // given
    Member following = MemberFixture.createCustomMember("followingMany@example.com", "followingNick");
    Member otherFollower1 = MemberFixture.createCustomMember("followone@example.com", "follower_one");
    Member otherFollower2 = MemberFixture.createCustomMember("followtwo@example.com", "follower_two");
    memberRepository.saveAll(List.of(following, otherFollower1, otherFollower2));

    followRepository.save(Follow.of(otherFollower1, following));
    followRepository.save(Follow.of(otherFollower2, following));

    // when
    List<Follow> followers = followRepository.findFollowersByFollowingId(following.getId());

    // then
    assertThat(followers).hasSize(2);
    assertThat(followers).extracting(f -> f.getFollower().getProfile().getNickname())
        .containsExactlyInAnyOrder("follower_one", "follower_two");
  }

  @Test
  @DisplayName("서로 팔로우(맞팔) 관계도 정상 동작한다")
  void mutualFollowTest() {
    followRepository.save(Follow.of(follower, following)); // follower → following
    followRepository.save(Follow.of(following, follower)); // following → follower

    List<Follow> f1 = followRepository.findFollowingsByFollowerId(follower.getId());
    List<Follow> f2 = followRepository.findFollowersByFollowingId(follower.getId());

    assertThat(f1.get(0).getFollowing().getProfile().getNickname()).isEqualTo("followingNick");
    assertThat(f2.get(0).getFollower().getProfile().getNickname()).isEqualTo("followingNick");
  }
}
