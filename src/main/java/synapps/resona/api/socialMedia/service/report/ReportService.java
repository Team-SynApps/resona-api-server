package synapps.resona.api.socialMedia.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.MemberException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.socialMedia.dto.report.request.CommentReportRequest;
import synapps.resona.api.socialMedia.dto.report.request.FeedReportRequest;
import synapps.resona.api.socialMedia.dto.report.request.ReplyReportRequest;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.comment.Reply;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.report.CommentReport;
import synapps.resona.api.socialMedia.entity.report.FeedReport;
import synapps.resona.api.socialMedia.entity.report.ReplyReport;
import synapps.resona.api.socialMedia.exception.CommentException;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.exception.ReplyException;
import synapps.resona.api.socialMedia.repository.comment.comment.CommentRepository;
import synapps.resona.api.socialMedia.repository.comment.reply.ReplyRepository;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.repository.report.ReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

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
  }
}