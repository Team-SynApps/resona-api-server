package com.synapps.resona.oauth.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProviderType {
    GOOGLE,
    APPLE,
    LOCAL;

    public static ProviderType of(String providerType) {
        return Arrays.stream(ProviderType.values())
                .filter(r -> r.toString().equals(providerType))
                .findAny()
                .orElse(LOCAL);
    }
}

