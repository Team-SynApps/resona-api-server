package com.synapps.atch.mysql.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DuplicateIdRequest {
    private String id;
}
