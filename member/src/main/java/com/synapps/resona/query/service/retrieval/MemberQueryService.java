package com.synapps.resona.query.service.retrieval;

import com.synapps.resona.common.dto.MemberDetailsResponse;
import com.synapps.resona.query.entity.MemberDocument;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import com.synapps.resona.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberDocumentRepository memberDocumentRepository;

    public MemberDetailsResponse getMemberDetailInfo(String email) {
        MemberDocument memberDocument = memberDocumentRepository.findByEmail(email)
            .orElseThrow(MemberException::memberNotFound);
        return MemberDetailsResponse.from(memberDocument.getMemberDetails());
    }

    public MemberDetailsResponse getMemberDetails(String email) {
        MemberDocument memberDocument = memberDocumentRepository.findByEmail(email)
            .orElseThrow(MemberException::memberNotFound);
        return MemberDetailsResponse.from(memberDocument.getMemberDetails());
    }
}
