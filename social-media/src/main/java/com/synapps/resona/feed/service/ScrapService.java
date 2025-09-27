package com.synapps.resona.feed.service;

import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.feed.dto.ScrapReadResponse;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.entity.Scrap;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.exception.ScrapException;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.feed.repository.ScrapRepository;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapService {

  private final ScrapRepository scrapRepository;
  private final FeedRepository feedRepository;
  private final MemberService memberService;
  private final MemberRepository memberRepository;

  @Transactional
  public ScrapReadResponse register(Long feedId, MemberDto memberDto) {
    Member member = memberRepository.findById(memberDto.getId())
        .orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    if (scrapRepository.existsByMemberAndFeedId(member, feed.getId())) {
      throw ScrapException.scrapAlreadyExist();
    }

    Scrap scrap = Scrap.of(member, feed, LocalDateTime.now());
    Scrap savedScrap = scrapRepository.save(scrap);
    return ScrapReadResponse.from(savedScrap);
  }

  @Transactional(readOnly = true)
  public ScrapReadResponse read(Long scrapId) {
    Scrap scrap = scrapRepository.findById(scrapId)
        .orElseThrow(ScrapException::scrapNotFound);
    return ScrapReadResponse.from(scrap);
  }

  @Transactional
  public void cancelScrap(Long feedId, Long memberId) {
    Scrap scrap = scrapRepository.findScrapByFeedId(feedId, memberId)
        .orElseThrow(ScrapException::scrapNotFound);
    scrap.softDelete();
  }

  @Transactional(readOnly = true)
  public CursorResult<ScrapReadResponse> readScrapsByCursor(String cursor, int size, MemberDto memberDto) {
    LocalDateTime cursorTime = (cursor == null || cursor.isBlank())
        ? LocalDateTime.now()
        : LocalDateTime.parse(cursor);

    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
    List<Scrap> scraps = scrapRepository.findScrapsByCursorAndMemberId(memberDto.getId(),
        cursorTime, pageable);

    boolean hasNext = scraps.size() > size;
    if (hasNext) {
      scraps = scraps.subList(0, size);
    }

    String nextCursor =
        scraps.isEmpty() ? null : scraps.get(scraps.size() - 1).getCreatedAt().toString();

    return new CursorResult<>(
        scraps.stream().map(ScrapReadResponse::from).toList(),
        hasNext,
        nextCursor
    );
  }
}