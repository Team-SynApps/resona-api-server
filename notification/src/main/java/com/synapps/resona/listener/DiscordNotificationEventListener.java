package com.synapps.resona.listener;

import com.synapps.resona.discord.service.DiscordNotificationService;
import com.synapps.resona.event.ReportCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordNotificationEventListener {

    private final DiscordNotificationService discordNotificationService;

    @EventListener
    public void handleReportCreatedEvent(ReportCreatedEvent event) {
        discordNotificationService.sendReportNotification(event.reportId(), event.reportType(), event.reporterId(), event.reportedId(), event.contentId(), event.reportCategoryDescription());
    }
}
