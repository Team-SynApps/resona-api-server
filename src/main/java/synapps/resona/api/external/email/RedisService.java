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
    private final RedisTemplate<String, Object> redisTemplate;

    //email을 key값 code를 value로 하여 3분동안 저장한다.
    public void setCode(String email,String code){
        ValueOperations<String, Object> valOperations = redisTemplate.opsForValue();
        //만료기간 10분
        valOperations.set(email,code,600, TimeUnit.SECONDS);
    }

    //key값인 email에 있는 value를 가져온다.
    public String getCode(String email) throws EmailException {
        ValueOperations<String, Object> valOperations = redisTemplate.opsForValue();
        Object code = valOperations.get(email);
        if(code == null){
            throw EmailException.blankCode();
        }
        return code.toString();
    }
}

