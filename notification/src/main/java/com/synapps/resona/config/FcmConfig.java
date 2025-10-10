package com.synapps.resona.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

@Configuration
public class FcmConfig {
  private final Logger logger = LoggerFactory.getLogger(FcmConfig.class);

  @Value("${fcm.path}")
  private String path;

  @PostConstruct
  public void initialize() {
    try {
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(
              GoogleCredentials.fromStream(new PathResource(path).getInputStream())
          )
          .build();
      FirebaseApp.initializeApp(options);
      logger.info("Fcm Configuration Success");
    } catch (IOException exception) {
      logger.error("Fcm Connection Error {}", exception.getMessage());
    }
  }
}