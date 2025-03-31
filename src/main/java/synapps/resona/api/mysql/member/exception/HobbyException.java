package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class HobbyException extends BaseException {
    public HobbyException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static HobbyException of(ErrorCode errorCode) {
        return new HobbyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static HobbyException hobbyNotFound() {
        return of(ErrorCode.HOBBY_NOT_FOUND);
    }
}
