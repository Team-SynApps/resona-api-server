package com.synapps.resona.query.service.retrieval;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.query.dto.MemberProfileDocumentDto;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRetrievalService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberDocumentRepository memberDocumentRepository;

    private static final String ONLINE_MEMBERS_KEY = "online_users";
    private static final String MEMBER_LAST_SEEN_KEY = "member_last_seen";

    public CursorResult<MemberProfileDocumentDto> getMembers(String cursor, int size, CountryCode countryCode) {
        // 온라인 Member ID 조회 및 필터링
        Set<String> onlineMemberValues = redisTemplate.opsForSet().members(ONLINE_MEMBERS_KEY);
        Set<Long> onlineMemberIds = onlineMemberValues.stream()
            .map(value -> value.split(":"))
            .filter(parts -> countryCode == null || parts.length < 2 || parts[1].equalsIgnoreCase(countryCode.name()))
            .map(parts -> Long.valueOf(parts[0]))
            .collect(Collectors.toSet());

        // 최근 접속 Member ID 조회
        Set<String> recentMemberValues = redisTemplate.opsForZSet().reverseRange(MEMBER_LAST_SEEN_KEY, 0, -1);
        if (recentMemberValues == null) {
            recentMemberValues = Collections.emptySet();
        }

        // ID 목록 병합, 필터링 및 우선순위 정렬
        List<Long> sortedMemberIds = Stream.concat(
            onlineMemberIds.stream(),
            recentMemberValues.stream()
                .map(value -> value.split(":"))
                .filter(parts -> countryCode == null || parts.length < 2 || parts[1].equalsIgnoreCase(countryCode.name()))
                .map(parts -> Long.valueOf(parts[0]))
        )
        .distinct()
        .collect(Collectors.toList());

        // 페이지네이션 적용
        long offset = (cursor == null || cursor.isEmpty()) ? 0 : Long.parseLong(cursor);

        List<Long> paginatedIds = sortedMemberIds.stream()
            .skip(offset)
            .limit(size)
            .collect(Collectors.toList());

        if (paginatedIds.isEmpty()) {
            return new CursorResult<>(Collections.emptyList(), false, null);
        }

        // MongoDB에서 상세 정보 조회 및 DTO 변환
        List<MemberProfileDocumentDto> members = memberDocumentRepository.findAllById(paginatedIds).stream()
            .map(doc -> {
                Double score = redisTemplate.opsForZSet().score(MEMBER_LAST_SEEN_KEY, doc.getId() + ":" + doc.getProfile().getCountryOfResidence().name());
                LocalDateTime lastAccessedAt = (score != null) 
                    ? LocalDateTime.ofInstant(Instant.ofEpochSecond(score.longValue()), ZoneOffset.UTC) 
                    : doc.getLastAccessedAt();
                return MemberProfileDocumentDto.from(doc, onlineMemberIds, lastAccessedAt);
            })
            .collect(Collectors.toList());

        // 다음 커서 생성
        long nextOffset = offset + paginatedIds.size();
        String nextCursor = null;
        if (nextOffset < sortedMemberIds.size()) {
            nextCursor = String.valueOf(nextOffset);
        }

        return new CursorResult<>(members, nextCursor != null, nextCursor);
    }
}
