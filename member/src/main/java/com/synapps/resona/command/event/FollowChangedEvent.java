package com.synapps.resona.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowChangedEvent {

    private final Long followerId;
    private final Long followingId;
    private final FollowAction action;

    public enum FollowAction {
        FOLLOW, UNFOLLOW
    }
}
