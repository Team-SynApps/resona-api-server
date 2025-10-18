package com.synapps.resona.query.service.retrieval;

import com.synapps.resona.query.dto.MemberDetailQueryDto;
import com.synapps.resona.query.dto.MemberDetailsQueryDto;
import com.synapps.resona.query.entity.MemberDocument;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import com.synapps.resona.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberDocumentRepository memberDocumentRepository;

    public MemberDetailQueryDto getMemberDetailInfo(String email) {
        MemberDocument memberDocument = memberDocumentRepository.findByEmail(email)
            .orElseThrow(MemberException::memberNotFound);
        return MemberDetailQueryDto.from(memberDocument);
    }

    public MemberDetailsQueryDto getMemberDetails(String email) {
        MemberDocument memberDocument = memberDocumentRepository.findByEmail(email)
            .orElseThrow(MemberException::memberNotFound);
        return MemberDetailsQueryDto.from(memberDocument.getMemberDetails());
    }
}
