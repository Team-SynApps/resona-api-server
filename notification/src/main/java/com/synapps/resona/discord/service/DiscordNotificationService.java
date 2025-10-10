package com.synapps.resona.discord.service;



import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

  @Value("${discord.webhook.report}")
  private String reportWebhookUrl;

  @Value("${discord.webhook.deploy}")
  private String deployWebhookUrl;

  public void sendReportNotification(Long reportId, String reportType, Long reporterId, Long reportedId, Long contentId, String reportCategoryDescription) {
    String message = createReportMessage(reportId, reportType, reporterId, reportedId, contentId, reportCategoryDescription);
    sendMessage(reportWebhookUrl, message);
  }

  public void sendDeployNotification(String message) {
    sendMessage(deployWebhookUrl, message);
  }

  private String createReportMessage(Long reportId, String reportType, Long reporterId, Long reportedId, Long contentId, String reportCategoryDescription) {
    return String.format(
        "**🚨 신규 신고 접수**\n\n" +
            "```\n" +
            "신고 ID: %d\n" +
            "신고 유형: %s\n" +
            "신고자 ID: %d\n" +
            "피신고자 ID: %d\n" +
            "콘텐츠 ID: %d\n" +
            "신고 사유: %s\n" +
            "```",
        reportId,
        reportType,
        reporterId,
        reportedId,
        contentId,
        reportCategoryDescription
    );
  }

  


  private void sendMessage(String webhookUrl, String message) {
    try {
      URL url = new URL(webhookUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; utf-8");
      conn.setDoOutput(true);

      String jsonPayload = String.format("{\"content\": \"%s\"}", message.replace("\n", "\\n"));

      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int responseCode = conn.getResponseCode();
      if (responseCode != 204) {
        // TODO: Handle error
      }
    } catch (Exception e) {
      // TODO: Handle exception
    }
  }
}
