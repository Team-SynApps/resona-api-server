package synapps.resona.api.socialMedia.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.discord.service.DiscordNotificationService;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.report.dto.request.CommentReportRequest;
import synapps.resona.api.socialMedia.report.dto.request.FeedReportRequest;
import synapps.resona.api.socialMedia.report.dto.request.ReplyReportRequest;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.comment.entity.Reply;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.report.entity.CommentReport;
import synapps.resona.api.socialMedia.report.entity.FeedReport;
import synapps.resona.api.socialMedia.report.entity.ReplyReport;
import synapps.resona.api.socialMedia.comment.exception.CommentException;
import synapps.resona.api.socialMedia.feed.exception.FeedException;
import synapps.resona.api.socialMedia.comment.exception.ReplyException;
import synapps.resona.api.socialMedia.comment.repository.comment.CommentRepository;
import synapps.resona.api.socialMedia.comment.repository.reply.ReplyRepository;
import synapps.resona.api.socialMedia.feed.repository.FeedRepository;
import synapps.resona.api.socialMedia.report.repository.ReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;
  private final DiscordNotificationService discordNotificationService;

  @Transactional
  public void reportFeed(FeedReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(request.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);

    FeedReport feedReport = FeedReport.of(reporter, reported, request.getReportCategory(), feed);
    reportRepository.save(feedReport);
    discordNotificationService.sendReportNotification(feedReport);
  }

  @Transactional
  public void reportComment(CommentReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);

    CommentReport commentReport = CommentReport.of(reporter, reported, comment,
        request.getReportCategory());
    reportRepository.save(commentReport);
    discordNotificationService.sendReportNotification(commentReport);
  }

  @Transactional
  public void reportReply(ReplyReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Reply reply = replyRepository.findById(request.getReplyId())
        .orElseThrow(ReplyException::replyNotFound);

    ReplyReport replyReport = ReplyReport.of(reporter, reported, reply,
        request.getReportCategory());
    reportRepository.save(replyReport);
    discordNotificationService.sendReportNotification(replyReport);
  }
}