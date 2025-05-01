package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.feedComplaint.FeedComplaintRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feedComplaint.FeedComplaint;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.repository.FeedComplaintRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedComplaintService {

  private final FeedComplaintRepository feedComplaintRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final MemberService memberService;

  @Transactional
  public FeedComplaint reportFeed(Long feedId, FeedComplaintRequest request) {
    String email = memberService.getMemberEmail();
    Member complainer = memberRepository.findByEmail(email)
        .orElseThrow(MemberException::memberNotFound);
    Feed feed = feedRepository.findWithMemberById(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedComplaint complaint = FeedComplaint.of(complainer, feed.getMember(), feed,
        request.getComplains(), request.isBlocked());
    return feedComplaintRepository.save(complaint);
  }
}
