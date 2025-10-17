package com.synapps.resona.test.controller;

import com.synapps.resona.test.service.TestRedisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "테스트용 API")
@RestController
@RequestMapping("/test/redis")
@RequiredArgsConstructor
public class TestRedisController {

    private final TestRedisService testRedisService;

    @DeleteMapping("/viewed-feeds/{memberId}")
    public ResponseEntity<Void> clearViewedFeeds(@PathVariable Long memberId) {
        testRedisService.clearViewedFeeds(memberId);
        return ResponseEntity.ok().build();
    }
}
