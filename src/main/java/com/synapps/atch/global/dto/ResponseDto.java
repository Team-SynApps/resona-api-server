package com.synapps.atch.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ResponseDto {
    private final Boolean success;
    private final List<?> result;
}