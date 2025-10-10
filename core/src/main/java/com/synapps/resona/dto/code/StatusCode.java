package com.synapps.resona.dto.code;

import org.springframework.http.HttpStatus;

public interface StatusCode {
  HttpStatus getStatus();
  String getMessage();
  int getStatusCode();
}
