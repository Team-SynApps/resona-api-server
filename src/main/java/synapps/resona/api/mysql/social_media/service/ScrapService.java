package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.Scrap;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.exception.ScrapNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.ScrapRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;

    @Transactional
    public Scrap register(Long feedId) throws FeedNotFoundException {
        Member member = memberService.getMember();
        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
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
        scrap.cancelScrap();

        return scrap;
    }

}
