package synapps.resona.api.socialMedia.service.restriction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.comment.Reply;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.restriction.CommentHide;
import synapps.resona.api.socialMedia.entity.restriction.FeedHide;
import synapps.resona.api.socialMedia.entity.restriction.ReplyHide;
import synapps.resona.api.socialMedia.exception.CommentException;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.exception.ReplyException;
import synapps.resona.api.socialMedia.repository.comment.comment.CommentRepository;
import synapps.resona.api.socialMedia.repository.comment.reply.ReplyRepository;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.repository.restriction.HideRepository;

@Service
@RequiredArgsConstructor
public class HideService {

  private final HideRepository hideRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

  @Transactional
  public void hideFeed(Long feedId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedHide feedHide = FeedHide.of(member, feed);
    hideRepository.save(feedHide);
  }

  @Transactional
  public void hideComment(Long commentId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);

    CommentHide commentHide = CommentHide.of(member, comment);
    hideRepository.save(commentHide);
  }

  @Transactional
  public void hideReply(Long replyId, MemberDto memberInfo) {
    Member member = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Reply reply = replyRepository.findById(replyId)
        .orElseThrow(ReplyException::replyNotFound);

    ReplyHide replyHide = ReplyHide.of(member, reply);
    hideRepository.save(replyHide);
  }
}
