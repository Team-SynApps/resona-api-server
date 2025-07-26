package synapps.resona.api.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API의 성공 응답을 문서화하기 위한 어노테이션. SuccessCodeSpec을 가집니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponse {
  SuccessCodeSpec value();
}