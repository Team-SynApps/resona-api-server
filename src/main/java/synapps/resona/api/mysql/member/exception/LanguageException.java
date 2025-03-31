package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class LanguageException extends BaseException {
    public LanguageException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static LanguageException of(ErrorCode errorCode) {
        return new LanguageException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static LanguageException languageNotFound() {
        return LanguageException.of(ErrorCode.LANGUAGE_NOT_FOUND);
    }
}
