package support.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import support.config.WebTestConfig;

/**
 * 웹(프레젠테이션) 계층 테스트를 위한 커스텀 애너테이션
 * - @WebMvcTest: MVC 관련 테스트 설정을 포함
 * - @ActiveProfiles("test"): 테스트 프로파일 지정
 * - @Import(WebTestConfig.class): 공통 웹 테스트 설정을 가져옴
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@ActiveProfiles("test")
@Import(WebTestConfig.class)
public @interface WebLayerTest {
}
