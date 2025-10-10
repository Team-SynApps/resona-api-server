package support;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import support.config.TestContainerConfig;
import support.config.TestSecurityConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(classes = TestConfiguration.class)
@Import({TestContainerConfig.class, TestSecurityConfig.class})
public abstract class IntegrationTestSupport {
}
