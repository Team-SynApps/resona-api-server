package com.synapps.resona.test.controller;

import com.synapps.resona.test.service.TestRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
