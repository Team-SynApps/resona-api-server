package com.synapps.resona.email;

import com.synapps.resona.email.exception.EmailException;
import com.synapps.resona.service.TempTokenService;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final MailSendService mailSendService;
  private final RedisService redisService;
  private final TempTokenService tempTokenService;

  public HashMap<String, Object> send(String email) {

    redisService.initialize(email);

    HashMap<String, Object> map = new HashMap<>();
    if (!redisService.isEmailSendAvailable(email)) {
      map.put("success", Boolean.FALSE);
      map.put("error", "일일 최대 발송 횟수를 초과했습니다.");
      map.put("remainingAttempts", 0);
      throw EmailException.sendTrialExceeded();
    }

    int number = mailSendService.sendMail(email);
    redisService.countSend(email);
    redisService.resetEmailCheckCount(email);
    String num = String.valueOf(number);

    redisService.setCode(email, num);
    map.put("success", Boolean.TRUE);
    map.put("remainingAttempts", redisService.getRemainingEmailSends(email));

    return map;
  }

  public List<?> verifyMailAndIssueToken(String email, String number) {
    if (!redisService.isEmailCheckAvailable(email)) {
      throw EmailException.verifyTrialExceeded();
    }

    if (number.equals(redisService.getCode(email))) {
      return List.of(tempTokenService.createTemporaryToken(email));
    }

    redisService.countCheck(email);
    HashMap<String, Object> map = new HashMap<>();
    int remainingCount = redisService.getRemainingEmailAuthenticates(email);
    throw EmailException.invalidEmailCode(remainingCount);
  }
}
