package synapps.resona.api.external.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import synapps.resona.api.external.email.exception.EmailException;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final int MAX_DAILY_SENDS = 10;
    private static final String SEND_COUNT_KEY = ":daily_send_count";
    private static final String NUMBER_CHECK_COUNT_KEY = ":daily_check_count";
    private final RedisTemplate<String, Object> redisEmailSendTemplate;
    private final RedisTemplate<String, Object> redisNumberCheckTemplate;

    // 이메일 발송 가능 여부 확인 및 카운트 증가
    public boolean canSendEmail(String email) throws EmailException {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();

        // 현재 발송 횟수 확인
        Object value = valOperations.get(countKey);
        int currentCount = 0;

        if (value != null) {
            currentCount = Integer.parseInt(value.toString());
            if (currentCount >= MAX_DAILY_SENDS) {
                return false;
            }
        }

        // 발송 횟수 증가
        if (value == null) {
            valOperations.set(countKey, "1", 24, TimeUnit.HOURS);
        } else {
            valOperations.set(countKey, String.valueOf(currentCount + 1), 24, TimeUnit.HOURS);
        }

        return true;
    }

    public boolean canCheckNumber(String email) {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisNumberCheckTemplate.opsForValue();

        // 현재 확인 횟수
        Object value = valOperations.get(countKey);
        int currentCount = 0;

        if (value != null) {
            currentCount = Integer.parseInt(value.toString());
            if (currentCount >= MAX_DAILY_SENDS) {
                return false;
            }
        }

        // 발송 횟수 증가
        if (value == null) {
            valOperations.set(countKey, "1", 24, TimeUnit.HOURS);
        } else {
            valOperations.set(countKey, String.valueOf(currentCount + 1), 24, TimeUnit.HOURS);
        }

        return true;
    }

    // 남은 발송 가능 횟수 조회
    public int getRemainingEmailSends(String email) {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object value = valOperations.get(countKey);

        if (value == null) {
            return MAX_DAILY_SENDS;
        }

        int currentCount = Integer.parseInt(value.toString());
        return Math.max(0, MAX_DAILY_SENDS - currentCount);
    }

    public int getRemainingNumberMatch(String email) {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valueOperations = redisNumberCheckTemplate.opsForValue();

        Object value = valueOperations.get(countKey);

        if (value == null) {
            return MAX_DAILY_SENDS;
        }

        int currentCount = Integer.parseInt(value.toString());
        return Math.max(0, MAX_DAILY_SENDS - currentCount);
    }

    // 인증 코드 저장
    public void setCode(String email, String code) {
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        valOperations.set(email, code, 600, TimeUnit.SECONDS);
    }

    // 인증 코드 조회
    public String getCode(String email) throws EmailException {
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object code = valOperations.get(email);
        if (code == null) {
            throw EmailException.blankCode();
        }
        // Redis의 TTL(Time To Live) 확인
        Long ttl = redisEmailSendTemplate.getExpire(email);

        if (ttl != null && ttl <= 0) {
            // 만료된 경우 예외
            throw EmailException.emailCodeExpired();
        }
        return code.toString();
    }
}