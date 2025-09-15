package synapps.resona.api.external.discord.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import synapps.resona.api.socialMedia.report.entity.CommentReport;
import synapps.resona.api.socialMedia.report.entity.FeedReport;
import synapps.resona.api.socialMedia.report.entity.ReplyReport;
import synapps.resona.api.socialMedia.report.entity.Report;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

  @Value("${discord.webhook.report}")
  private String reportWebhookUrl;

  @Value("${discord.webhook.deploy}")
  private String deployWebhookUrl;

  public void sendReportNotification(Report report) {
    String message = createReportMessage(report);
    sendMessage(reportWebhookUrl, message);
  }

  public void sendDeployNotification(String message) {
    sendMessage(deployWebhookUrl, message);
  }

  private String createReportMessage(Report report) {
    String reportType = "알 수 없음";
    Long contentId = null;

    if (report instanceof FeedReport) {
      reportType = "피드";
      contentId = ((FeedReport) report).getFeed().getId();
    } else if (report instanceof CommentReport) {
      reportType = "댓글";
      contentId = ((CommentReport) report).getComment().getId();
    } else if (report instanceof ReplyReport) {
      reportType = "답글";
      contentId = ((ReplyReport) report).getReply().getId();
    }

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
        report.getId(),
        reportType,
        report.getReporter().getId(),
        report.getReported().getId(),
        contentId,
        report.getCategory().getDescription()
    );
  }

  private Long getReportedContentId(Report report) {
    if (report instanceof synapps.resona.api.socialMedia.report.entity.FeedReport) {
      return ((synapps.resona.api.socialMedia.report.entity.FeedReport) report).getFeed().getId();
    }
    if (report instanceof synapps.resona.api.socialMedia.report.entity.CommentReport) {
      return ((synapps.resona.api.socialMedia.report.entity.CommentReport) report).getComment().getId();
    }
    if (report instanceof synapps.resona.api.socialMedia.report.entity.ReplyReport) {
      return ((synapps.resona.api.socialMedia.report.entity.ReplyReport) report).getReply().getId();
    }
    return null;
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
