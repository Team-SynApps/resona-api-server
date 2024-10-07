package synapps.resona.api.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "Member not found"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEM002", "Duplicate email"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEM003", "Invalid password"),
    INVALID_TIMESTAMP(HttpStatus.UNAUTHORIZED, "MEM004", "Invalid timestamp"),

    // auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "Expired token"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "Invalid refresh token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH004", "Refresh token not found"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH005", "Token not found"),

    //email
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "EMAIL001", "Invalid email code"),
    EMAIL_SEND_FAILED(HttpStatus.CONFLICT, "EMAIL002", "Email send failed"),
    ;

    private HttpStatus status;
    private final String code;
    private final String message;

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
