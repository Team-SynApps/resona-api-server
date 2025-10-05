package com.synapps.resona.comment.query.service;

import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.repository.CommentDocumentRepository;
import com.synapps.resona.query.member.service.MemberStateService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

  private final CommentDocumentRepository commentDocumentRepository;
  private final MemberStateService memberStateService;
  private final CommentStatusCalculator statusCalculator;

  public Page<CommentDto> getCommentsForFeed(Long feedId, Long viewerId, Pageable pageable) {
    ViewerContext viewerContext = new ViewerContext(
        viewerId,
        memberStateService.getHiddenCommentIds(viewerId),
        memberStateService.getHiddenReplyIds(viewerId),
        memberStateService.getBlockedMemberIds(viewerId)
    );

    Page<CommentDocument> commentPage = commentDocumentRepository.findByFeedIdOrderByCreatedAtDesc(feedId, pageable);

    return commentPage.map(comment -> toCommentDto(comment, viewerContext));
  }

  private CommentDto toCommentDto(CommentDocument comment, ViewerContext context) {
    CommentDisplayStatus commentStatus = statusCalculator.determineCommentStatus(comment, context);
    String displayContent = statusCalculator.getDisplayContent(comment.getContent(), commentStatus);

    List<ReplyDto> replyDtos = comment.getReplies().stream()
        .map(reply -> {
          CommentDisplayStatus replyStatus = statusCalculator.determineReplyStatus(reply, context);
          String replyContent = statusCalculator.getDisplayContent(reply.getContent(), replyStatus);
          return ReplyDto.from(reply, replyStatus, replyContent);
        })
        .collect(Collectors.toList());

    return CommentDto.from(comment, commentStatus, displayContent, replyDtos);
  }
}