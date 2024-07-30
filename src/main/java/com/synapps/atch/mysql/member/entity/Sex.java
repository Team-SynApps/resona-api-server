package com.synapps.atch.mysql.member.entity;

import com.synapps.atch.oauth.entity.ProviderType;

import java.util.Arrays;

public enum Sex {
    MAN, WOMAN, NO;

    public static Sex of(String sex) {
        return Arrays.stream(Sex.values())
                .filter(r -> r.toString().equals(sex))
                .findAny()
                .orElse(NO);
    }
}
