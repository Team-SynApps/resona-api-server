package synapps.resona.api;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import synapps.resona.api.global.config.FcmConfig;

@ActiveProfiles("test")
@SpringBootTest
class ResonaAPIServerTests {

  @MockBean
  private FcmConfig fcmConfig;

  @MockBean
  private FirebaseApp firebaseApp;

  @Test
  void contextLoads() {
  }

}
