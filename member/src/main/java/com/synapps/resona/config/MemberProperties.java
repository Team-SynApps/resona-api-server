package com.synapps.resona.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "member.celebrity")
public record MemberProperties(long followerThreshold) {

}
