package synapps.resona.api.member.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.MemberRefreshToken;

@MySQLRepository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

  MemberRefreshToken findByMemberEmail(String MemberEmail);

  MemberRefreshToken findByMemberEmailAndRefreshToken(String MemberEmail, String refreshToken);
}
