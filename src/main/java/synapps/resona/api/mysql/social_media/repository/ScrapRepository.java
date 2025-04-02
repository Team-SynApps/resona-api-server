package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Scrap;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    boolean existsByIdAndMember(Long scrapId, Member member);
}
