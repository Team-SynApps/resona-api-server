package synapps.resona.api.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER001", "Internal Server Error"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Invalid Input"),

    // member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "Member not found"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEM002", "Duplicate email"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEM003", "Invalid password"),
    INVALID_TIMESTAMP(HttpStatus.UNAUTHORIZED, "MEM004", "Invalid timestamp"),
    UNAUTHENTICATED_REQUEST(HttpStatus.FORBIDDEN, "MEM005", "Forbidden approach"),

    // auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "Expired token"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "Invalid refresh token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH004", "Refresh token not found"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH005", "Token not found"),
    INVALID_CLIENT(HttpStatus.UNAUTHORIZED, "AUTH006", "Invalid client"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH000", "Invalid token"),
    NOT_EXPIRED(HttpStatus.NOT_ACCEPTABLE, "AUTH007", "Access token not Expired"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH008", "You do not have permission to access this resource."),

    //email
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "EMAIL001", "Invalid email code"),
    EMAIL_SEND_FAILED(HttpStatus.CONFLICT, "EMAIL002", "Email send failed"),
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL003", "Email code not found"),
    SEND_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL004", "Email send trial exceeded"),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "EMAIL005", "Email code does not match"),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL006", "Email code expired"),
    VERIFY_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL007", "Email verify trial exceeded"),

    //profile
    PROFILE_INPUT_INVALID(HttpStatus.CONFLICT, "PROFILE001", "Invalid profile"),

    TIMESTAMP_INVALID(HttpStatus.CONFLICT, "TIMESTAMP001", "Invalid timestamp"),

    // file
    FILE_EMPTY_EXCEPTION(HttpStatus.CONFLICT, "FILE001", "File is empty"),

    // language
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "LANG001", "Invalid Language code"),

    // hobby
    HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "HOB001", "Hobby Not Found"),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }
}
