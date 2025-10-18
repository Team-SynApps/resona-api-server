package com.synapps.resona.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HobbyNameUpdatedEvent {

    private final Long memberId;
    private final String oldName;
    private final String newName;
}
