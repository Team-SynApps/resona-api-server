package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;

public class InvalidTimeStampException extends BaseException {
    public InvalidTimeStampException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public static InvalidTimeStampException of(String message, HttpStatus status, String errorCode) {
        return new InvalidTimeStampException(message, status, errorCode);
    }
}
