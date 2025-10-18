package com.synapps.resona.event;

import java.time.LocalDateTime;

public record MemberActivityEvent(Long memberId, LocalDateTime timestamp) {
}
