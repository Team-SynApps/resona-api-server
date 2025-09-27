package com.synapps.resona.repository.member;

import com.synapps.resona.entity.member.MemberRefreshToken;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

  MemberRefreshToken findByMemberEmail(String MemberEmail);

  MemberRefreshToken findByMemberEmailAndRefreshToken(String MemberEmail, String refreshToken);
}
