package com.synapps.resona.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.FileInputStream;
import java.io.InputStream; // FileInputStream 대신 InputStream 사용
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource; // ClassPathResource import

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
            if (privateKeyPath.startsWith("classpath:")) {
              String actualPath = privateKeyPath.replace("classpath:", "");
              return new ClassPathResource(actualPath).getInputStream();
            } else {
              return new FileInputStream(privateKeyPath);
            }
          } catch (Exception e) {
            throw new RuntimeException("Private key 파일을 읽을 수 없습니다: " + privateKeyPath, e);
          }
        })
        .region(Region.fromRegionId(region))
        .build();

    return ObjectStorageClient.builder()
        .build(provider);
  }
}