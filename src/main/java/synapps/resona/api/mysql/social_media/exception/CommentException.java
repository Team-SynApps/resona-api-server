package synapps.resona.api.mysql.social_media.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class CommentException extends BaseException {
    protected CommentException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static CommentException of(ErrorCode errorCode) {
        return new CommentException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static CommentException commentNotFound() {
        return of(ErrorCode.COMMENT_NOT_FOUND);
    }
}
