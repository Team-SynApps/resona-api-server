// synapps.resona.api.global.annotation.SuccessCodeSpec.java

package synapps.resona.api.global.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import synapps.resona.api.global.dto.code.SuccessCode;

@Retention(RetentionPolicy.RUNTIME)
public @interface SuccessCodeSpec {
  Class<? extends SuccessCode> enumClass();

  String code();

  boolean cursor() default false;

  Class<?> responseClass() default Void.class;

  Class<?> listElementClass() default Void.class;
}