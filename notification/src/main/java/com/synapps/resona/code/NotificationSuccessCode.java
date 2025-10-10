package com.synapps.resona.code;

import com.synapps.resona.dto.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum NotificationSuccessCode implements SuccessCode {

    // Notification
    REGISTER_FCM_TOKEN_SUCCESS(HttpStatus.OK, "FCM token registered successfully."),
    GET_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "Successfully retrieved notification settings."),
    UPDATE_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "Notification settings updated successfully."),
    GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "Successfully retrieved notification list."),
    READ_NOTIFICATION_SUCCESS(HttpStatus.OK, "Notification read successfully."),
    DELETE_NOTIFICATION_SUCCESS(HttpStatus.OK, "Notification deleted successfully.");


    private final HttpStatus status;
    private final String message;

    NotificationSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

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
        return this.status.value();
    }
}
