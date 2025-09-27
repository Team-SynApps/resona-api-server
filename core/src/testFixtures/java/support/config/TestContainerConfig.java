package support.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

  @Bean
  @ServiceConnection
  public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
        .withStartupTimeout(Duration.ofMinutes(5));
  }
//
//  @Bean
//  @ServiceConnection
//  public MySQLContainer<?> mySQLContainer() {
//    return new MySQLContainer<>(DockerImageName.parse("mysql:8"));
//  }
//
//  @Bean
//  @ServiceConnection
//  public GenericContainer<?> redisContainer() {
//    return new GenericContainer<>(DockerImageName.parse("redis:7"))
//        .withExposedPorts(6379);
//  }

}