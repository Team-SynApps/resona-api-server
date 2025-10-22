package com.synapps.resona.query.service.retrieval;

import com.synapps.resona.common.dto.ProfileResponse;
import com.synapps.resona.query.entity.MemberDocument;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import com.synapps.resona.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileQueryService {

    private final MemberDocumentRepository memberDocumentRepository;

    public ProfileResponse readProfile(String email) {
        MemberDocument memberDocument = memberDocumentRepository.findByEmail(email)
            .orElseThrow(MemberException::memberNotFound);
        return ProfileResponse.from(memberDocument.getProfile());
    }

    public boolean checkDuplicateTag(String tag) {
        return memberDocumentRepository.existsByProfileTag(tag);
    }
}
