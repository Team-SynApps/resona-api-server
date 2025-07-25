package synapps.resona.api.global.error.core;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.StatusCode;

public interface ErrorCode extends StatusCode {
    HttpStatus getStatus();
    String getMessage();
    int getStatusCode();
    String getCustomCode();

    default String getMessage(Object... args){
        return String.format(this.getMessage(), args);
    }
}
