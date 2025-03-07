package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.MemberRefreshToken;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

    MemberRefreshToken findByMemberEmail(String MemberEmail);

    MemberRefreshToken findByMemberEmailAndRefreshToken(String MemberEmail, String refreshToken);
}
