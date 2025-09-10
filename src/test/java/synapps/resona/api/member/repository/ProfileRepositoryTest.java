package synapps.resona.api.member.repository;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.config.TestQueryDslConfig;

@Transactional
@DataJpaTest
@Import(TestQueryDslConfig.class)
class ProfileRepositoryTest {

}