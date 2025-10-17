package com.synapps.resona.query.service;

import com.synapps.resona.query.dto.MemberProfileQueryDto;
import com.synapps.resona.query.entity.MemberDocument;
import com.synapps.resona.query.entity.MemberStateDocument;
import com.synapps.resona.query.repository.MemberDocumentRepository;
import com.synapps.resona.query.repository.MemberStateDocumentRepository;
import com.synapps.resona.exception.MemberException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowQueryService {

    private final MemberStateDocumentRepository memberStateDocumentRepository;
    private final MemberDocumentRepository memberDocumentRepository;

    public List<MemberProfileQueryDto> getFollowers(Long memberId) {
        MemberStateDocument memberState = memberStateDocumentRepository.findById(memberId)
            .orElseThrow(MemberException::memberNotFound);

        List<Long> followerIds = memberState.getFollowerIds().stream().toList();

        return memberDocumentRepository.findAllById(followerIds).stream()
            .map(MemberProfileQueryDto::from)
            .collect(Collectors.toList());
    }

    public List<MemberProfileQueryDto> getFollowings(Long memberId) {
        MemberStateDocument memberState = memberStateDocumentRepository.findById(memberId)
            .orElseThrow(MemberException::memberNotFound);

        List<Long> followingIds = memberState.getFollowingIds().stream().toList();

        return memberDocumentRepository.findAllById(followingIds).stream()
            .map(MemberProfileQueryDto::from)
            .collect(Collectors.toList());
    }

    public Page<Long> getFollowerIds(Long memberId, Pageable pageable) {
        MemberStateDocument memberState = memberStateDocumentRepository.findById(memberId)
            .orElseThrow(MemberException::memberNotFound);

        List<Long> followerIds = memberState.getFollowerIds().stream().toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), followerIds.size());

        return new PageImpl<>(followerIds.subList(start, end), pageable, followerIds.size());
    }
}
