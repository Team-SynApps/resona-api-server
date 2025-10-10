package support.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 관련 테스트를 위한 공통 설정 클래스
 * @EnableJpaAuditing: 테스트 시에도 JPA Auditing 기능을 활성화합니다.
 */
@TestConfiguration
@EnableJpaAuditing
public class JpaTestConfig {
  // 테스트에 필요한 특별한 빈이 있다면 여기에 정의할 수 있습니다.
  // 예를 들어, AuditorAware의 테스트용 구현체 등
}