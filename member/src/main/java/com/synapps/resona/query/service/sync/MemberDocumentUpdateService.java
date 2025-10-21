package com.synapps.resona.query.service.sync;

import com.synapps.resona.command.event.HobbyAddedEvent;
import com.synapps.resona.command.event.HobbyNameUpdatedEvent;
import com.synapps.resona.command.event.HobbyRemovedEvent;
import com.synapps.resona.command.event.MemberDetailsUpdatedEvent;
import com.synapps.resona.command.event.ProfileUpdatedEvent;
import com.synapps.resona.query.entity.MemberDocument;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDocumentUpdateService {

    private final MongoTemplate mongoTemplate;

    public void updateProfile(ProfileUpdatedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.getMemberId()));
        Update update = new Update()
            .set("profile.tag", event.getTag())
            .set("profile.nickname", event.getNickname())
            .set("profile.nationality", event.getNationality())
            .set("profile.countryOfResidence", event.getCountryOfResidence())
            .set("profile.nativeLanguages", event.getNativeLanguages())
            .set("profile.interestingLanguages", event.getInterestingLanguages())
            .set("profile.profileImageUrl", event.getProfileImageUrl())
            .set("profile.backgroundImageUrl", event.getBackgroundImageUrl())
            .set("profile.age", event.getAge())
            .set("profile.birth", event.getBirth())
            .set("profile.gender", event.getGender())
            .set("profile.comment", event.getComment())
            .set("modifiedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, MemberDocument.class);
    }

    public void updateMemberDetails(MemberDetailsUpdatedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.getMemberId()));
        Update update = new Update()
            .set("memberDetails.timezone", event.getTimezone())
            .set("memberDetails.phoneNumber", event.getPhoneNumber())
            .set("memberDetails.mbti", event.getMbti())
            .set("memberDetails.aboutMe", event.getAboutMe())
            .set("memberDetails.location", event.getLocation())
            .set("modifiedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, MemberDocument.class);
    }

    public void addHobby(HobbyAddedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.getMemberId()));
        Update update = new Update().addToSet("memberDetails.hobbies", event.getHobbyName())
            .set("modifiedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, MemberDocument.class);
    }

    public void removeHobby(HobbyRemovedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.getMemberId()));
        Update update = new Update().pull("memberDetails.hobbies", event.getHobbyName())
            .set("modifiedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, MemberDocument.class);
    }

    public void updateHobbyName(HobbyNameUpdatedEvent event) {
        Query query = Query.query(Criteria.where("_id").is(event.getMemberId()));
        Update update = new Update()
            .pull("memberDetails.hobbies", event.getOldName())
            .addToSet("memberDetails.hobbies", event.getNewName())
            .set("modifiedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, MemberDocument.class);
    }
}
