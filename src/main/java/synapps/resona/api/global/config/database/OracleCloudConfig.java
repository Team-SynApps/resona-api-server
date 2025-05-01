package synapps.resona.api.global.config.database;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oci.config")
@Getter
@Setter
public class OracleCloudConfig {

  private String userId;
  private String tenancyId;
  private String fingerprint;
  private String privateKeyPath;
  private String region;

  @Bean
  public ObjectStorage objectStorageClient() throws Exception {
    AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
        .userId(userId)
        .tenantId(tenancyId)
        .fingerprint(fingerprint)
        .privateKeySupplier(() -> {
          try {
            return new FileInputStream(privateKeyPath);
          } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
          }
        })
        .region(Region.fromRegionId(region))
        .build();

    return ObjectStorageClient.builder()
        .build(provider);
  }
}

