package synapps.resona.api.global.dto.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode extends StatusCode {
    HttpStatus getStatus();
    String getMessage();
    int getStatusCode();
    String getCustomCode();

    default String getMessage(Object... args){
        return String.format(this.getMessage(), args);
    }
}
