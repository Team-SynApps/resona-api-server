package com.synapps.atch.global.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MetaDataDto {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path;
    private final String apiVersion;
    private final String serverName;
    private final String requestId;
//    private Long processingTime;

    public MetaDataDto(int status, String message, String path, String apiVersion, String serverName) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.apiVersion = apiVersion;
        this.serverName = serverName;
        this.requestId = generateRequestId();
    }

    private static String generateRequestId() {
        return java.util.UUID.randomUUID().toString();
    }

//    public void setProcessingTime(Long processingTime) {
//        this.processingTime = processingTime;
//    }

    public static MetaDataDto createSuccessMetaData(String path, String apiVersion, String serverName) {
        return new MetaDataDto(200, "Success", path, apiVersion, serverName);
    }
    public static MetaDataDto createErrorMetaData(int status, String message, String path, String apiVersion, String serverName) {
        return new MetaDataDto(status, message, path, apiVersion, serverName);
    }
}