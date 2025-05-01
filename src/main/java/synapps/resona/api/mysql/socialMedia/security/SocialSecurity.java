package synapps.resona.api.mysql.socialMedia.security;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.repository.CommentLikesRepository;
import synapps.resona.api.mysql.socialMedia.repository.CommentRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;
import synapps.resona.api.mysql.socialMedia.repository.LikesRepository;
import synapps.resona.api.mysql.socialMedia.repository.MentionRepository;
import synapps.resona.api.mysql.socialMedia.repository.ReplyRepository;
import synapps.resona.api.mysql.socialMedia.repository.ScrapRepository;

@Component("socialSecurity")
@RequiredArgsConstructor
public class SocialSecurity {

  private static final Logger log = LogManager.getLogger(SocialSecurity.class);
  private final MemberService memberService;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final MentionRepository mentionRepository;
  private final ScrapRepository scrapRepository;
  private final ReplyRepository replyRepository;
  private final LikesRepository likesRepository;
  private final CommentLikesRepository commentLikesRepository;

  public boolean isFeedMemberProperty(Long feedId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return feedRepository.existsByIdAndMember(feedId, member);
  }

  public boolean isCommentMemberProperty(Long commentId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return commentRepository.existsByIdAndMember(commentId, member);
  }

  public boolean isScrapMemberProperty(Long scrapId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return scrapRepository.existsByIdAndMember(scrapId, member);
  }

  public boolean isMentionMemberProperty(Long mentionId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return mentionRepository.existsByIdAndMember(mentionId, member);
  }

  public boolean isReplyMemberProperty(Long replyId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return replyRepository.existsByIdAndMember(replyId, member);
  }

  public boolean isLikeMemberProperty(Long likeId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return likesRepository.existsByIdAndMember(likeId, member);
  }

  public boolean isCommentLikesMemberProperty(Long commentLikesId) {
    Member member = memberService.getMemberUsingSecurityContext();
    return commentLikesRepository.existsByIdAndMember(commentLikesId, member);
  }
}
