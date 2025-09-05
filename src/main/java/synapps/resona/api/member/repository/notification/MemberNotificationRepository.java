package synapps.resona.api.member.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.notification.entity.MemberNotification;

import java.util.List;

@MySQLRepository
public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Long> {

    List<MemberNotification> findByMemberAndIdLessThanOrderByIdDesc(Member member, Long id, Pageable pageable);
}
