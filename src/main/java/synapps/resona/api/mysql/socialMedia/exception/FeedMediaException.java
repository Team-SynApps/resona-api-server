package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class FeedMediaException extends BaseException {
    protected FeedMediaException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static FeedMediaException of(ErrorCode errorCode) {
        return new FeedMediaException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static FeedMediaException imageNotFound() {
        return of(ErrorCode.IMAGE_NOT_FOUND);
    }
}
