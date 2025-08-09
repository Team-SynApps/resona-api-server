package synapps.resona.api.global.config.database;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import synapps.resona.api.global.annotation.DatabaseRepositories.MongoDBRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.RedisRepository;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories(
    basePackages = "synapps.resona.api",
    includeFilters = @Filter(type = FilterType.ANNOTATION, classes = RedisRepository.class),
    excludeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = MySQLRepository.class),
        @Filter(type = FilterType.ANNOTATION, classes = MongoDBRepository.class)
    }
)
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    //key,value를 Email,code로 구현할거기에 둘다 String으로 직렬화 해도 문제없다.
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}

