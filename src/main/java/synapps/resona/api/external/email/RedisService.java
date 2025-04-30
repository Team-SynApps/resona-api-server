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
    private static final int MAX_DAILY_SENDS = 3;
    private static final int MAX_DAILY_AUTHENTICATE = 5;
    private static final int MAX_TIME_LIMIT_OVER = 200;
    private static final int MAX_TIME_LIMIT = 100;
    private static final String SEND_COUNT_KEY = ":daily_send_count";
    private static final String NUMBER_CHECK_COUNT_KEY = ":daily_check_count";
    private final RedisTemplate<String, Object> redisEmailSendTemplate;
    private final RedisTemplate<String, Object> redisNumberCheckTemplate;

    /**
     * 이메일 전송 가능한지 확인하는 메서드
     * 전송 횟수가 일일 전송 최대 횟수를 초과하면 false 리턴
     *
     * @param email 이메일
     * @return 이메일 전송 가능 여부
     */
    public boolean isEmailSendAvailable(String email) {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();

        Object value = valOperations.get(countKey);


        int currentCount = Integer.parseInt(value.toString());
        return currentCount < MAX_DAILY_SENDS;
    }
//    // redis에 email에 매칭하는 카운트가 없는 경우
//        if (value == null) {
//        valOperations.set(countKey, "1", 24, TimeUnit.HOURS);
//        return true;
//    }

    /**
     * 이메일 검증가능한지 확인하는 메서드
     * 검증 횟수가 일일 최대 횟수를 초과하면 false 리턴
     *
     * @param email 이메일
     * @return 이메일 검증 가능 여부
     */
    public boolean isEmailCheckAvailable(String email) throws EmailException {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisNumberCheckTemplate.opsForValue();

        // 현재 확인 횟수
        Object value = valOperations.get(countKey);
        if (value == null) {
            throw EmailException.emailCodeNotFound();
        }

        int currentCount = Integer.parseInt(value.toString());

        return MAX_DAILY_AUTHENTICATE > currentCount;
    }

    /**
     * 남은 이메일 전송 횟수를 리턴
     *
     * @param email 이메일
     * @return 남은 이메일 전송 횟수
     */
    public int getRemainingEmailSends(String email) {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object value = valOperations.get(countKey);

        if (value == null) {
            return MAX_DAILY_SENDS;
        }

        int currentCount = Integer.parseInt(value.toString());
        return MAX_DAILY_SENDS - currentCount;
    }

    /**
     * 남은 이메일 검증 횟수를 리턴
     *
     * @param email 이메일
     * @return 남은 이메일 검증 횟수
     */
    public int getRemainingEmailAuthenticates(String email) {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valueOperations = redisNumberCheckTemplate.opsForValue();

        Object value = valueOperations.get(countKey);

        if (value == null) {
            return MAX_DAILY_AUTHENTICATE;
        }

        int currentCount = Integer.parseInt(value.toString());
        return MAX_DAILY_AUTHENTICATE - currentCount;
    }

    /**
     * 이메일 코드를 저장하는 메서드
     *
     * @param email 이메일
     * @param code  저장할 코드번호
     */
    public void setCode(String email, String code) {
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        valOperations.set(email, code, MAX_TIME_LIMIT_OVER, TimeUnit.SECONDS);
    }

    /**
     * 이메일 인증 코드를 조회하는 메서드
     *
     * @param email 이메일
     * @return 이메일에 매칭되는 코드 번호
     * @throws EmailException 인증시간이 넘어가면 예외 발생
     */
    public String getCode(String email) throws EmailException {
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object code = valOperations.get(email);

        // 이메일 인증코드가 20분이 넘어가거나, 생성되지 않았을 때
        if (code == null) {
            throw EmailException.emailCodeNotFound();
        }

        // Redis의 TTL(Time To Live) 확인
        Long ttl = redisEmailSendTemplate.getExpire(email);

        if (ttl == null) {
            throw EmailException.emailCodeExpired();
        }

        // 이메일 인증시간 10분이 지난 경우
        if (ttl <= MAX_TIME_LIMIT) {
            // 만료된 경우 예외
            throw EmailException.emailCodeExpired();
        }
        return code.toString();
    }

    public void countSend(String email) {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object value = valOperations.get(countKey);

        int currentCount = Integer.parseInt(value.toString());
        valOperations.set(countKey, String.valueOf(currentCount + 1), 24, TimeUnit.HOURS);
    }

    public void countCheck(String email) {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valueOperations = redisNumberCheckTemplate.opsForValue();
        Object value = valueOperations.get(countKey);

        int currentCount = Integer.parseInt(value.toString());
        valueOperations.set(countKey, String.valueOf(currentCount + 1), 24, TimeUnit.HOURS);
    }

    public void initialize(String email) {
        String countKey = email + SEND_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisEmailSendTemplate.opsForValue();
        Object value = valOperations.get(countKey);

        if (value == null) {
            valOperations.set(countKey, "0", 24, TimeUnit.HOURS);
        }
    }

    public void resetEmailCheckCount(String email) {
        String countKey = email + NUMBER_CHECK_COUNT_KEY;
        ValueOperations<String, Object> valOperations = redisNumberCheckTemplate.opsForValue();

        valOperations.set(countKey, "0", 24, TimeUnit.HOURS);
    }
}