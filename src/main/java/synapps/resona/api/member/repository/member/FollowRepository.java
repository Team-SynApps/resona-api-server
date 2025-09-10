package synapps.resona.api.member.repository.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Follow;
import synapps.resona.api.member.entity.member.Member;

@MySQLRepository
public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsByFollowerAndFollowing(Member follower, Member following);

  Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

  @Query("SELECT DISTINCT f FROM Follow f " +
      "JOIN FETCH f.following m " +
      "JOIN FETCH m.profile " +
      "WHERE f.follower.id = :memberId")
  List<Follow> findFollowingsByFollowerId(@Param("memberId") Long memberId);

  @Query("SELECT DISTINCT f FROM Follow f " +
      "JOIN FETCH f.follower m " +
      "JOIN FETCH m.profile " +
      "WHERE f.following.id = :memberId")
  List<Follow> findFollowersByFollowingId(@Param("memberId") Long memberId);

  long countByFollower(Member follower);

  long countByFollowing(Member following);
}