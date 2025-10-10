package com.synapps.resona.code;

import com.synapps.resona.dto.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "Notification not found"),
    NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "N002", "Notification setting not found"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "N003", "Member not found"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "N004", "You are not allowed to perform this action");

    private final HttpStatus status;
    private final String customCode;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }

    @Override
    public String getCustomCode() {
        return this.customCode;
    }
}
