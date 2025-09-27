package com.synapps.resona.annotation;

import com.synapps.resona.dto.code.SuccessCode;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SuccessCodeSpec {
  Class<? extends SuccessCode> enumClass();

  String code();

  boolean cursor() default false;

  Class<?> responseClass() default Void.class;

  Class<?> listElementClass() default Void.class;
}