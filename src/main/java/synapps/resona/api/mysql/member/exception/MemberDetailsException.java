package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class MemberDetailsException extends BaseException {

    protected MemberDetailsException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static MemberDetailsException of(ErrorCode errorCode) {
        return new MemberDetailsException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static MemberDetailsException memberDetailsNotFound() {
        return of(ErrorCode.MEMBER_DETAILS_NOT_FOUND);
    }
}
