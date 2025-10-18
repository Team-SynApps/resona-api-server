package com.synapps.resona.query.listener;

import com.synapps.resona.event.MemberActivityEvent;
import com.synapps.resona.query.service.presence.MemberPresenceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberActivityEventListener {

    private final MemberPresenceCommandService memberPresenceCommandService;

    @Async
    @EventListener
    public void handleMemberActivity(MemberActivityEvent event) {
        memberPresenceCommandService.updateLastSeen(event.memberId(), event.timestamp());
    }
}
