package com.synapps.atch.mysql.member.repository;

import com.synapps.atch.mysql.member.entity.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

    MemberRefreshToken findByMemberEmail(String MemberEmail);

    MemberRefreshToken findByMemberEmailAndRefreshToken(String MemberEmail, String refreshToken);
}
