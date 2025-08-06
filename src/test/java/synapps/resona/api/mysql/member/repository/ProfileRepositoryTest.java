package synapps.resona.api.mysql.member.repository;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.config.TestQueryDslConfig;
import synapps.resona.api.global.config.database.QueryDslConfig;

@Transactional
@DataJpaTest
@Import(TestQueryDslConfig.class)
class ProfileRepositoryTest {

}