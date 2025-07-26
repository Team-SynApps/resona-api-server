package synapps.resona.api.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드에 적용할 최종 어노테이션. ErrorCodeSpec의 배열을 가집니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorSpec {
  ErrorCodeSpec[] value();
}