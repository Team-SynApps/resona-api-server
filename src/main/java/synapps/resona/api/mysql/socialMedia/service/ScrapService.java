package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.scrap.ScrapReadResponse;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.exception.ScrapException;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;
import synapps.resona.api.mysql.socialMedia.repository.ScrapRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public Scrap register(Long feedId) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);

        if (scrapRepository.existsByMemberAndFeedId(member, feedId)) {
            throw ScrapException.scrapAlreadyExist();
        }

        Scrap scrap = Scrap.of(member, feed, LocalDateTime.now());
        return scrapRepository.save(scrap);
    }

    public Scrap read(Long scrapId) {
        return scrapRepository.findById(scrapId).orElseThrow(ScrapException::scrapNotFound);
    }

    @Transactional
    public Scrap cancelScrap(Long scrapId) {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(ScrapException::scrapNotFound);
        scrap.softDelete();
        return scrap;
    }

    @Transactional(readOnly = true)
    public CursorResult<ScrapReadResponse> readScrapsByCursor(String cursor, int size) {
        MemberDto memberDto = memberService.getMember();
        LocalDateTime cursorTime = (cursor == null || cursor.isBlank())
                ? LocalDateTime.now()
                : LocalDateTime.parse(cursor);

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Scrap> scraps = scrapRepository.findScrapsByCursorAndMemberId(memberDto.getId(), cursorTime, pageable);

        boolean hasNext = scraps.size() > size;
        if (hasNext) {
            scraps = scraps.subList(0, size);
        }

        String nextCursor = scraps.isEmpty() ? null : scraps.get(scraps.size() - 1).getCreatedAt().toString();

        return new CursorResult<>(
                scraps.stream().map(ScrapReadResponse::from).toList(),
                hasNext,
                nextCursor
        );

    }

}
