package com.synapps.atch.test.controller;

import com.synapps.atch.test.service.AwsDynamoDbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lyrics")
@Tag(name = "테스트 컨트롤러", description = "테스트 API입니다.")
public class AwsDynamoDbController {

    private final AwsDynamoDbService awsDynamoDbService;

    @Autowired
    public AwsDynamoDbController(AwsDynamoDbService awsDynamoDbService) {
        this.awsDynamoDbService = awsDynamoDbService;
    }

    @PostMapping
    public ResponseEntity<String> createLyric(@RequestBody LyricRequest request) {
        try {
            awsDynamoDbService.createItem(request.getId(), request.getLyric());
            return ResponseEntity.status(HttpStatus.CREATED).body("Lyric created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create lyric");
        }
    }

    // Define a request model for receiving JSON data
    public static class LyricRequest {
        private String id;
        private String lyric;

        // Constructor, getters, and setters
        public LyricRequest() {}

        public LyricRequest(String id, String lyric) {
            this.id = id;
            this.lyric = lyric;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }
}
