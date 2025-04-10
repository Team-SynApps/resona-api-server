package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.Scrap;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.exception.ScrapNotFoundException;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;
import synapps.resona.api.mysql.socialMedia.repository.ScrapRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public Scrap register(Long feedId){
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
        Scrap scrap = Scrap.of(member, feed, LocalDateTime.now());
        scrapRepository.save(scrap);

        return scrap;
    }

    public Scrap read(Long scrapId) throws ScrapNotFoundException {
        return scrapRepository.findById(scrapId).orElseThrow(ScrapNotFoundException::new);
    }

    @Transactional
    public Scrap cancelScrap(Long scrapId) throws ScrapNotFoundException {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(ScrapNotFoundException::new);
        scrap.softDelete();
        return scrap;
    }

}
