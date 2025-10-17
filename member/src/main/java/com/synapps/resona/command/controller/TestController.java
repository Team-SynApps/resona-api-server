package com.synapps.resona.command.controller;

import com.synapps.resona.command.dto.request.auth.RegisterRequest;
import com.synapps.resona.command.dto.response.MemberRegisterResponseDto;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.command.service.TempTokenService;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.profile.CountryCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "테스트용 API")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
// @Profile("dev") 
public class TestController {

    private final MemberService memberService;
    private final TempTokenService tempTokenService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ONLINE_MEMBERS_KEY = "online_users";
    private static final String MEMBER_LAST_SEEN_KEY = "member_last_seen";

    @Operation(summary = "테스트용 멤버 대량 생성 및 redis 접속중 및 최근 접속 설정")
    @PostMapping("/create-members")
    public ResponseEntity<String> createTestMembers(@RequestBody CreateMembersRequest request) {
        List<Long> memberIds = new ArrayList<>();

        IntStream.range(0, request.getCount()).forEach(i -> {
            String email = request.getEmailPrefix() + i + "@example.com";
            String nickname = request.getNicknamePrefix() + i;
            String tag = "";
            if (i < 26) {
                tag = request.getTagPrefix() + (char)('a' + i % 26);
            }else {
                tag = request.getTagPrefix() + (char)('a' + i % 26) + (char)('a' + i / 26);
            }

            tempTokenService.createTemporaryToken(email);

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(email);
            registerRequest.setPassword("password123!");
            registerRequest.setNickname(nickname);
            registerRequest.setTag(tag);
            registerRequest.setBirth("2000-01-01");
            registerRequest.setProfileImageUrl("");
            registerRequest.setCountryOfResidence(CountryCode.KR);
            registerRequest.setNationality(CountryCode.KR);
            registerRequest.setNativeLanguageCodes(Set.of(Language.ko.getCode()));
            registerRequest.setInterestingLanguageCodes(Set.of(Language.en.getCode()));
            registerRequest.setTimezone(9);
            registerRequest.setSocialLogin(false);

            MemberRegisterResponseDto response = memberService.signUp(registerRequest);
            memberIds.add(response.getMemberId());
        });

        // Populate Redis
        if (request.getOnlineCount() > 0) {
            List<String> onlineIds = memberIds.stream()
                .limit(request.getOnlineCount())
                .map(String::valueOf)
                .toList();
            redisTemplate.opsForSet().add(ONLINE_MEMBERS_KEY, onlineIds.toArray(new String[0]));
        }

        if (request.getRecentCount() > 0) {
            LocalDateTime now = LocalDateTime.now();
            IntStream.range(0, request.getRecentCount()).forEach(i -> {
                if (request.getOnlineCount() + i < memberIds.size()) {
                    Long memberId = memberIds.get(request.getOnlineCount() + i);
                    LocalDateTime timestamp = now.minusMinutes(i);
                    redisTemplate.opsForZSet().add(MEMBER_LAST_SEEN_KEY, String.valueOf(memberId), timestamp.toEpochSecond(ZoneOffset.UTC));
                }
            });
        }

        return ResponseEntity.ok(request.getCount() + " members created and Redis populated successfully.");
    }

    @Getter
    public static class CreateMembersRequest {
        private int count;
        private String emailPrefix;
        private String nicknamePrefix;
        private String tagPrefix;
        private int onlineCount;
        private int recentCount;
    }
}
