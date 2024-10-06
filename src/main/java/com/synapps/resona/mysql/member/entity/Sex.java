package com.synapps.resona.mysql.member.entity;

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
