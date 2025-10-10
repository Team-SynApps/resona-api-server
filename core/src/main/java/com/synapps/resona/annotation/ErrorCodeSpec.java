package com.synapps.resona.annotation;

import com.synapps.resona.dto.code.ErrorCode;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCodeSpec {
  Class<? extends ErrorCode> enumClass();

  String[] codes();
}