package com.synapps.atch.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private boolean success;

    private List<?> result;
    public ResponseDto() {}
}