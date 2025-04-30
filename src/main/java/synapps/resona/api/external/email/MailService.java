package synapps.resona.api.external.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.external.email.exception.EmailException;
import synapps.resona.api.mysql.member.service.TempTokenService;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final MailSendService mailSendService;
    private final RedisService redisService;
    private final TempTokenService tempTokenService;

    public HashMap<String, Object> send(String email) throws EmailException {

        redisService.initialize(email); // 이메일 인증이 초기화 된 경우 동작

        HashMap<String, Object> map = new HashMap<>();
        if (!redisService.isEmailSendAvailable(email)) {
            map.put("success", Boolean.FALSE);
            map.put("error", "일일 최대 발송 횟수를 초과했습니다.");
            map.put("remainingAttempts", 0);
            throw EmailException.sendTrialExceeded();
        }

        int number = mailSendService.sendMail(email); // 메일 전송
        redisService.countSend(email);
        redisService.resetEmailCheckCount(email);
        String num = String.valueOf(number);

        redisService.setCode(email, num);
        map.put("success", Boolean.TRUE);
        map.put("remainingAttempts", redisService.getRemainingEmailSends(email));

        return map;
    }

    public List<?> verifyMailAndIssueToken(String email, String number) throws EmailException {
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
