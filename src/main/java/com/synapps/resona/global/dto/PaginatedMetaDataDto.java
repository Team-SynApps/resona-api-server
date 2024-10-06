package com.synapps.resona.global.dto;

import lombok.Getter;

@Getter
public class PaginatedMetaDataDto extends MetaDataDto {
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PaginatedMetaDataDto(int status, String message, String path, String apiVersion, String serverName,
                                int page, int size, long totalElements, int totalPages) {
        super(status, message, path, apiVersion, serverName);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static PaginatedMetaDataDto createSuccessPaginatedMetaData(String path, String apiVersion,
                                                                      String serverName, int page, int size,
                                                                      long totalElements, int totalPages) {
        return new PaginatedMetaDataDto(200, "Success", path, apiVersion, serverName,
                page, size, totalElements, totalPages);
    }
}