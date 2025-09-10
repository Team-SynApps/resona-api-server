package synapps.resona.api.support;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import synapps.resona.api.config.TestContainerConfig;
import synapps.resona.api.global.config.FcmConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
//@ImportTestcontainers(TestContainerConfig.class)
public abstract class IntegrationTestSupport {

  @MockBean
  private FcmConfig fcmConfig;

  @MockBean
  private FirebaseApp firebaseApp;
}
