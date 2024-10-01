package com.synapps.atch.external.email;

import com.synapps.atch.external.email.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "메일을 보낼 구글 이메일";
    private static int number;

    // 랜덤으로 숫자 생성
    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    //이메일 내용 수정 필요
    public MimeMessage createMail(String mail) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("Email Certification");
            String body = "";
            body += "<h3>" + "This is your certification number" + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "Thank you" + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            EmailException.emailSendFailed();
        }

        return message;
    }

    public int sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);

        return number;
    }
}
