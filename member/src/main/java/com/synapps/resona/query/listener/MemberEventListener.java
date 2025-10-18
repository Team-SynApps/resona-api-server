package com.synapps.resona.query.listener;

import com.synapps.resona.command.event.FollowChangedEvent;
import com.synapps.resona.command.event.HobbyAddedEvent;
import com.synapps.resona.command.event.HobbyNameUpdatedEvent;
import com.synapps.resona.command.event.HobbyRemovedEvent;
import com.synapps.resona.command.event.MemberCreatedEvent;
import com.synapps.resona.command.event.MemberDetailsUpdatedEvent;
import com.synapps.resona.command.event.ProfileUpdatedEvent;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.query.event.MemberUnblockedEvent;
import com.synapps.resona.query.service.sync.MemberDocumentUpdateService;
import com.synapps.resona.query.service.sync.MemberReadModelSyncService;
import com.synapps.resona.query.service.sync.MemberStateDocumentUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberEventListener {

    private final MemberReadModelSyncService memberReadModelSyncService;
    private final MemberDocumentUpdateService memberDocumentUpdateService;
    private final MemberStateDocumentUpdateService memberStateDocumentUpdateService;

    @TransactionalEventListener
    public void handleMemberCreatedEvent(MemberCreatedEvent event) {
        memberReadModelSyncService.createMemberDocument(event);
    }

    @TransactionalEventListener
    public void handleProfileUpdatedEvent(ProfileUpdatedEvent event) {
        memberDocumentUpdateService.updateProfile(event);
    }

    @TransactionalEventListener
    public void handleMemberDetailsUpdatedEvent(MemberDetailsUpdatedEvent event) {
        memberDocumentUpdateService.updateMemberDetails(event);
    }

    @TransactionalEventListener
    public void handleFollowChangedEvent(FollowChangedEvent event) {
        memberStateDocumentUpdateService.updateFollow(event);
    }

    @TransactionalEventListener
    public void handleHobbyAddedEvent(HobbyAddedEvent event) {
        memberDocumentUpdateService.addHobby(event);
    }

    @TransactionalEventListener
    public void handleHobbyRemovedEvent(HobbyRemovedEvent event) {
        memberDocumentUpdateService.removeHobby(event);
    }

    @TransactionalEventListener
    public void handleHobbyNameUpdatedEvent(HobbyNameUpdatedEvent event) {
        memberDocumentUpdateService.updateHobbyName(event);
    }

    @TransactionalEventListener
    public void handleMemberBlockedEvent(MemberBlockedEvent event) {
        memberStateDocumentUpdateService.addBlock(event);
    }

    @TransactionalEventListener
    public void handleMemberUnblockedEvent(MemberUnblockedEvent event) {
        memberStateDocumentUpdateService.removeBlock(event);
    }
}
