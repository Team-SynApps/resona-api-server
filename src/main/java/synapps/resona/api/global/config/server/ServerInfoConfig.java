package synapps.resona.api.global.config.server;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ServerInfoConfig {

  @Value("${api.version}")
  private String apiVersion;

  @Value("${server.name}")
  private String serverName;

  @PostConstruct
  public void init() {
    validateApiVersion();
  }

  private void validateApiVersion() {
    if (apiVersion == null || !apiVersion.matches("v\\d+")) {
      throw new IllegalStateException(
          "Invalid API version format. It should be in the format 'vX' where X is a number.");
    }
  }

  public String getVersionNumber() {
    return apiVersion.substring(1);
  }
}