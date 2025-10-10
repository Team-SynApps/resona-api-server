package support.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

  @Bean
  @ServiceConnection(name = "mongo")
  public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
        .withStartupTimeout(Duration.ofMinutes(5));
  }

  @Bean
  @ServiceConnection(name = "mysql")
  public MySQLContainer<?> mySQLContainer() {
    return new MySQLContainer<>(DockerImageName.parse("mysql:8"));
  }

  @Bean
  @ServiceConnection(name = "redis")
  public RedisContainer redisContainer() {
    return new RedisContainer(DockerImageName.parse("redis:7"));
  }

}