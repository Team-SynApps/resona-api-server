package com.synapps.resona.query.service.sync;

import com.synapps.resona.command.event.FollowChangedEvent;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.query.event.MemberUnblockedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStateDocumentUpdateService {

    private final MongoTemplate mongoTemplate;

    public void updateFollow(FollowChangedEvent event) {
        if (event.getAction() == FollowChangedEvent.FollowAction.FOLLOW) {
            addFollow(event.getFollowerId(), event.getFollowingId());
        } else {
            removeFollow(event.getFollowerId(), event.getFollowingId());
        }
    }

    public void addBlock(MemberBlockedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.blockerId()));
        Update update = new Update().addToSet("blockedMemberIds", event.blockedId());
        mongoTemplate.updateFirst(query, update, MemberStateDocument.class);
    }

    public void removeBlock(MemberUnblockedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.blockerId()));
        Update update = new Update().pull("blockedMemberIds", event.unblockedId());
        mongoTemplate.updateFirst(query, update, MemberStateDocument.class);
    }

    private void addFollow(Long followerId, Long followingId) {
        Query followerQuery = Query.query(Criteria.where("_id").is(followerId));
        Update followerUpdate = new Update().addToSet("followingIds", followingId);
        mongoTemplate.updateFirst(followerQuery, followerUpdate, MemberStateDocument.class);

        Query followingQuery = Query.query(Criteria.where("_id").is(followingId));
        Update followingUpdate = new Update().addToSet("followerIds", followerId);
        mongoTemplate.updateFirst(followingQuery, followingUpdate, MemberStateDocument.class);
    }

    private void removeFollow(Long followerId, Long followingId) {
        Query followerQuery = Query.query(Criteria.where("_id").is(followerId));
        Update followerUpdate = new Update().pull("followingIds", followingId);
        mongoTemplate.updateFirst(followerQuery, followerUpdate, MemberStateDocument.class);

        Query followingQuery = Query.query(Criteria.where("_id").is(followingId));
        Update followingUpdate = new Update().pull("followerIds", followerId);
        mongoTemplate.updateFirst(followingQuery, followingUpdate, MemberStateDocument.class);
    }
}
