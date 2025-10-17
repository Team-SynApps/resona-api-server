package com.synapps.resona.query.service.presence;

import com.synapps.resona.query.repository.MemberDocumentRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberPresenceCommandService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberDocumentRepository memberDocumentRepository;
    private static final String MEMBER_LAST_SEEN_KEY = "member_last_seen";

    public void updateLastSeen(Long memberId, LocalDateTime timestamp) {
        memberDocumentRepository.findById(memberId).ifPresent(memberDoc -> {
            String countryCode = memberDoc.getProfile().getCountryOfResidence().name();
            String value = memberId + ":" + countryCode;
            redisTemplate.opsForZSet().add(MEMBER_LAST_SEEN_KEY, value, timestamp.toEpochSecond(ZoneOffset.UTC));
        });
    }
}
