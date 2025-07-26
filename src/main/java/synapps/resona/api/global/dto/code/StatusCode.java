package synapps.resona.api.global.dto.code;

import org.springframework.http.HttpStatus;

public interface StatusCode {
  HttpStatus getStatus();
  String getMessage();
  int getStatusCode();
}
