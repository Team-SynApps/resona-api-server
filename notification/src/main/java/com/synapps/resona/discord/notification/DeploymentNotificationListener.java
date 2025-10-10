package com.synapps.resona.discord.notification;


import com.synapps.resona.discord.service.DiscordNotificationService;
import com.synapps.resona.config.server.ServerInfoConfig;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeploymentNotificationListener {

  private final DiscordNotificationService discordNotificationService;
  private final ServerInfoConfig serverInfoConfig;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    String message = String.format(
        "🚀 **Resona API 서버 배포 성공!**\n\n" +
            "> **서버 이름:** %s\n" +
            "> **API 버전:** %s\n" +
            "> **배포 시각:** %s",
        serverInfoConfig.getServerName(),
        serverInfoConfig.getApiVersion(),
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    );
    discordNotificationService.sendDeployNotification(message);
  }
}