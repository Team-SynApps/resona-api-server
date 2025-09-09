package synapps.resona.api.socialMedia.service.feed;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.socialMedia.dto.scrap.ScrapReadResponse;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.feed.Scrap;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.exception.ScrapException;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.repository.feed.ScrapRepository;

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