package synapps.resona.api.external.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import synapps.resona.api.external.email.exception.EmailException;

@Service
@RequiredArgsConstructor
public class MailSendService {

  private static final String senderEmail = "synapps99@gmail.com";
  private static int number;
  private final JavaMailSender javaMailSender;

  // 랜덤으로 숫자 생성
  public static void createNumber() {
    number = (int) (Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
  }

  // TODO: 이메일 내용 수정 필요
  public MimeMessage createMail(String mail) throws EmailException {
    if (mail == null || mail.isEmpty()) {
      throw new IllegalArgumentException("수신자의 이메일 주소가 유효하지 않습니다.");
    }

    if (!isValidEmail(mail)) {
      throw new IllegalArgumentException("메일이 유효하지 않습니다.");
    }

    createNumber();
    MimeMessage message = javaMailSender.createMimeMessage();

    try {
      message.setFrom(senderEmail);
      message.setRecipients(MimeMessage.RecipientType.TO, mail);
      message.setSubject("Resona: 인증번호 안내");

      String body = "";
      body += "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eaeaea; border-radius: 10px;'>";
      body += "  <h2 style='color: #333;'>Resona에 오신 것을 환영합니다!</h2>";
      body += "  <p style='font-size: 16px; color: #555;'>아래 인증번호를 입력하여 인증을 완료하세요:</p>";
      body += "  <div style='background-color: #f9f9f9; padding: 15px; border-radius: 8px; text-align: center;'>";
      body +=
          "    <h1 style='color: #4CAF50; cursor: pointer;' onclick='copyToClipboard()'>" + number
              + "</h1>";
      body += "  </div>";
      body += "  <p style='font-size: 14px; color: #777;'>Resona를 이용해 주셔서 감사합니다.</p>";
      body += "</div>";

      message.setText(body, "UTF-8", "html");
    } catch (MailSendException | MessagingException e) {
      throw EmailException.emailSendFailed();
    }
    return message;
  }

  public int sendMail(String mail) throws EmailException {
    try {
      MimeMessage message = createMail(mail);
      javaMailSender.send(message);
      return number;
    } catch (MailException | MessagingException e) {
      throw EmailException.emailSendFailed();
    }
  }

  private boolean isValidEmail(String email) {
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
      return true;
    } catch (AddressException e) {
      return false;
    }
  }
}
