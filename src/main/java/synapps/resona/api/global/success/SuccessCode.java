package synapps.resona.api.global.success;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.StatusCode;

public interface SuccessCode extends StatusCode {
    HttpStatus getStatus();
    String getMessage();
    int getStatusCode();
}
