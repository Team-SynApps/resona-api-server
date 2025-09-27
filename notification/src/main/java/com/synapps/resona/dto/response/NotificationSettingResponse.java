package com.synapps.resona.dto.response;

import com.synapps.resona.entity.MemberNotificationSetting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {

    private final boolean friendRequestEnabled;

    private final boolean marketingEnabled;

    private final boolean serviceNotificationEnabled;

    private final boolean socialNotificationEnabled;

    public static NotificationSettingResponse to(
        MemberNotificationSetting memberNotificationSetting) {
        return NotificationSettingResponse.builder()
            .friendRequestEnabled(memberNotificationSetting.isFriendRequestEnabled())
            .marketingEnabled(memberNotificationSetting.isMarketingEnabled())
            .serviceNotificationEnabled(memberNotificationSetting.isServiceNotificationEnabled())
            .socialNotificationEnabled(memberNotificationSetting.isSocialNotificationEnabled())
            .build();
    }
}
