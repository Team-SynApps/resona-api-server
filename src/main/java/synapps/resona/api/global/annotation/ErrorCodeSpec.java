package synapps.resona.api.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import synapps.resona.api.global.dto.code.ErrorCode;

/**
 * 문서화할 에러 코드의 Enum 클래스와 코드 목록을 정의하는 어노테이션
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCodeSpec {
  Class<? extends ErrorCode> enumClass();

  String[] codes();
}