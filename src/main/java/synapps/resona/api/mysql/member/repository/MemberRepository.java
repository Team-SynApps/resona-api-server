package synapps.resona.api.mysql.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  @Query("SELECT DISTINCT m FROM Member m " +
      "JOIN FETCH m.accountInfo " +
      "JOIN FETCH m.memberDetails " +
      "JOIN FETCH m.profile p " +
      "LEFT JOIN FETCH p.nativeLanguages " +
      "LEFT JOIN FETCH p.interestingLanguages " +
      "WHERE m.email = :email")
  Optional<Member> findWithAllRelationsByEmail(@Param("email") String email);

  @Query("SELECT m FROM Member m " +
      "JOIN FETCH m.accountInfo " +
      "WHERE m.email = :email")
  Optional<Member> findWithAccountInfoByEmail(@Param("email") String email);

  @Query("SELECT m.profile FROM Member m WHERE m.email = :email")
  Optional<Profile> findProfileByEmail(@Param("email") String email);

  @Query("SELECT m.accountInfo FROM Member m WHERE m.email =:email")
  Optional<AccountInfo> findAccountInfoByEmail(@Param("email") String email);

  @Query("SELECT m.memberDetails FROM Member m WHERE m.email = :email")
  Optional<MemberDetails> findMemberDetailsByEmail(@Param("email") String email);
}
