package com.synapps.resona.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HobbyAddedEvent {

    private final Long memberId;
    private final String hobbyName;
}
