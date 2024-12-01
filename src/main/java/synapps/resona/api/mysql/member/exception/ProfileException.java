package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class ProfileException extends BaseException {
    public ProfileException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static ProfileException of(ErrorCode errorCode) {
        return new ProfileException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static ProfileException invalidProfile() {
        return of(ErrorCode.PROFILE_INPUT_INVALID);
    }
}
