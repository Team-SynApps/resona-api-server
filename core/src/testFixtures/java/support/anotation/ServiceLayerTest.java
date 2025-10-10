package support.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

/**
 * 서비스 계층 단위 테스트를 위한 커스텀 애너테이션
 * - @ExtendWith(MockitoExtension.class): JUnit5와 Mockito를 연동하여
 * @Mock, @InjectMocks 등의 기능을 활성화합니다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-test.yml")
public @interface ServiceLayerTest {
}