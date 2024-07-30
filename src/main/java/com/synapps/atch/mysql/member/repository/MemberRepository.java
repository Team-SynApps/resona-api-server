package com.synapps.atch.mysql.member.repository;

import com.synapps.atch.mysql.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Member findByEmail(String email);
}
