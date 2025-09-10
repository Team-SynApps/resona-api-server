package synapps.resona.api.mysql.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.member.Follow;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MBTI;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.member.repository.member.FollowRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.repository.feed.dsl.FeedExpressions;

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
    follower = createMember("follower@example.com", "팔로워닉네임");
    following = createMember("following@example.com", "팔로잉닉네임");

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
    assertThat(result.getFollowing().getProfile().getNickname()).isEqualTo("팔로잉닉네임");
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
    assertThat(result.getFollower().getProfile().getNickname()).isEqualTo("팔로워닉네임");
  }

  @Test
  @DisplayName("한 사람이 여러 명을 팔로우했을 때, 모든 following의 프로필을 fetch 한다")
  void findFollowingsByFollowerId_multiple() {
    // given
    Member follower = createMember("followerMany@example.com", "팔로워닉네임");
    Member another = createMember("another@example.com", "다른닉네임");
    Member another2 = createMember("another2@example.com", "또다른닉네임");
    memberRepository.saveAll(List.of(follower, another, another2));

    followRepository.save(Follow.of(follower, another));
    followRepository.save(Follow.of(follower, another2));

    // when
    List<Follow> followings = followRepository.findFollowingsByFollowerId(follower.getId());

    // then
    assertThat(followings).hasSize(2);
    assertThat(followings).extracting(f -> f.getFollowing().getProfile().getNickname())
        .containsExactlyInAnyOrder("다른닉네임", "또다른닉네임");
  }

  @Test
  @DisplayName("여러 사람이 같은 사람을 팔로우했을 때, 모든 follower의 프로필을 fetch 한다")
  void findFollowersByFollowingId_multiple() {
    // given
    Member following = createMember("followingMany@example.com", "팔로잉닉네임");
    Member otherFollower1 = createMember("1@example.com", "팔로워1");
    Member otherFollower2 = createMember("2@example.com", "팔로워2");
    memberRepository.saveAll(List.of(following, otherFollower1, otherFollower2));

    followRepository.save(Follow.of(otherFollower1, following));
    followRepository.save(Follow.of(otherFollower2, following));

    // when
    List<Follow> followers = followRepository.findFollowersByFollowingId(following.getId());

    // then
    assertThat(followers).hasSize(2);
    assertThat(followers).extracting(f -> f.getFollower().getProfile().getNickname())
        .containsExactlyInAnyOrder("팔로워1", "팔로워2");
  }

  @Test
  @DisplayName("서로 팔로우(맞팔) 관계도 정상 동작한다")
  void mutualFollowTest() {
    followRepository.save(Follow.of(follower, following)); // follower → following
    followRepository.save(Follow.of(following, follower)); // following → follower

    List<Follow> f1 = followRepository.findFollowingsByFollowerId(follower.getId());
    List<Follow> f2 = followRepository.findFollowersByFollowingId(follower.getId());

    assertThat(f1.get(0).getFollowing().getProfile().getNickname()).isEqualTo("팔로잉닉네임");
    assertThat(f2.get(0).getFollower().getProfile().getNickname()).isEqualTo("팔로잉닉네임");
  }

  private Member createMember(String email, String nickname) {
    AccountInfo accountInfo = AccountInfo.empty();
    MemberDetails memberDetails = MemberDetails.of(0, "01011111111", MBTI.ENFJ, "about me",
        "location");
    Profile profile = Profile.of(
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        nickname,
        nickname + "-tag",
        "http://profile.img/" + nickname,
        "2000-01-01"
    );

    return Member.of(accountInfo, memberDetails, profile, email, "password", LocalDateTime.now());
  }
}
