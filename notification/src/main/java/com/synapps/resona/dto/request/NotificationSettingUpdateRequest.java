package com.synapps.resona.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationSettingUpdateRequest {

    private boolean friendRequestEnabled;

    private boolean marketingEnabled;

    private boolean serviceNotificationEnabled;

    private boolean socialNotificationEnabled;
}
