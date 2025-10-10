package support.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import support.config.JpaTestConfig;

/**
 * JPA 영속성 계층 테스트를 위한 커스텀 애너테이션
 * * - @DataJpaTest: JPA 관련 테스트 설정을 모두 포함
 * - @ActiveProfiles("test"): 테스트 환경 프로파일을 'test'로 설정
 * - @ContextConfiguration: JpaTestConfig 설정을 가져와 JPA Auditing 등을 활성화
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = JpaTestConfig.class)
public @interface PersistenceLayerTest {
}