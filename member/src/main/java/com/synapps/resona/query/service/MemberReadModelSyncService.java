package com.synapps.resona.query.service;

import com.synapps.resona.command.event.MemberCreatedEvent;
import com.synapps.resona.query.entity.MemberDocument;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberReadModelSyncService {

    private final MemberDocumentRepository memberDocumentRepository;

    public void createMemberDocument(MemberCreatedEvent event) {
        MemberDocument.AccountInfoEmbed accountInfo = MemberDocument.AccountInfoEmbed.builder()
            .roleType(event.accountInfo().roleType())
            .status(event.accountInfo().status())
            .build();

        MemberDocument.MemberDetailsEmbed memberDetails = MemberDocument.MemberDetailsEmbed.builder()
            .timezone(event.memberDetailsInfo().timezone())
            .phoneNumber(event.memberDetailsInfo().phoneNumber())
            .mbti(event.memberDetailsInfo().mbti())
            .aboutMe(event.memberDetailsInfo().aboutMe())
            .location(event.memberDetailsInfo().location())
            .hobbies(event.memberDetailsInfo().hobbies())
            .build();

        MemberDocument.ProfileEmbed profile = MemberDocument.ProfileEmbed.builder()
            .tag(event.profileInfo().tag())
            .nickname(event.profileInfo().nickname())
            .nationality(event.profileInfo().nationality())
            .countryOfResidence(event.profileInfo().countryOfResidence())
            .nativeLanguages(event.profileInfo().nativeLanguages())
            .interestingLanguages(event.profileInfo().interestingLanguages())
            .profileImageUrl(event.profileInfo().profileImageUrl())
            .backgroundImageUrl(event.profileInfo().backgroundImageUrl())
            .age(event.profileInfo().age())
            .gender(event.profileInfo().gender())
            .comment(event.profileInfo().comment())
            .build();

        MemberDocument memberDocument = MemberDocument.builder()
            .id(event.memberId())
            .email(event.email())
            .accountInfo(accountInfo)
            .memberDetails(memberDetails)
            .profile(profile)
            .providers(event.providers())
            .lastAccessedAt(event.lastAccessedAt())
            .build();

        memberDocumentRepository.save(memberDocument);
    }
}
