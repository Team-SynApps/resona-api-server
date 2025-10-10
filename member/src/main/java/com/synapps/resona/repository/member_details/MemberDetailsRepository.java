package com.synapps.resona.repository.member_details;

import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {

}
