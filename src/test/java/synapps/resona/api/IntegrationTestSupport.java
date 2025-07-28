package synapps.resona.api;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import synapps.resona.api.global.config.FcmConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @MockBean
  private FcmConfig fcmConfig;

  @MockBean
  private FirebaseApp firebaseApp;
}
