package com.synapps.resona.comment.query.service;

import com.synapps.resona.comment.command.entity.CommentDisplayStatus;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.dto.ReplyDto;
import com.synapps.resona.comment.query.dto.ViewerContext;
import com.synapps.resona.common.event.CommentTranslationUpdatedEvent;
import com.synapps.resona.common.event.ReplyTranslationUpdatedEvent;
import com.synapps.resona.comment.query.entity.CommentDocument;
import com.synapps.resona.comment.query.entity.ReplyEmbed;
import com.synapps.resona.comment.query.repository.CommentDocumentRepository;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import com.synapps.resona.query.service.MemberStateService;
import com.synapps.resona.translation.service.TranslationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentRetrievalService {

  private final CommentDocumentRepository commentDocumentRepository;
  private final MemberStateService memberStateService;
  private final CommentStatusCalculator statusCalculator;
  private final TranslationService translationService;

  private final ApplicationEventPublisher applicationEventPublisher;


  public Page<CommentDto> getCommentsForFeed(Long feedId, Long viewerId, Language targetLanguage, Pageable pageable) {
    ViewerContext viewerContext = new ViewerContext(
        viewerId,
        memberStateService.getHiddenCommentIds(viewerId),
        memberStateService.getHiddenReplyIds(viewerId),
        memberStateService.getBlockedMemberIds(viewerId)
    );

    Page<CommentDocument> commentPage = commentDocumentRepository.findByFeedIdOrderByCreatedAtDesc(feedId, pageable);

    return commentPage.map(comment -> toCommentDto(comment, viewerContext, targetLanguage));
  }

  private CommentDto toCommentDto(CommentDocument comment, ViewerContext context, Language targetLanguage) {
    CommentDisplayStatus commentStatus = statusCalculator.determineCommentStatus(comment, context);

    String translatedCommentContent = comment.getTranslations().stream()
        .filter(t -> t.languageCode().equals(targetLanguage.getCode()))
        .findFirst()
        .map(Translation::content)
        .orElseGet(() -> {
          if (comment.getLanguage().equals(targetLanguage)) {
            return comment.getContent();
          }
          String translatedContent = translationService.translateForRealTime(comment.getContent(), comment.getLanguage(), targetLanguage);
          applicationEventPublisher.publishEvent(new CommentTranslationUpdatedEvent(comment.getCommentId(), translatedContent, targetLanguage));
          return translatedContent;
        });

    String displayContent = statusCalculator.getDisplayContent(translatedCommentContent, commentStatus);

    List<ReplyDto> replyDtos = comment.getReplies().stream()
        .map(reply -> toReplyDto(reply, comment, context, targetLanguage))
        .collect(Collectors.toList());

    return CommentDto.from(comment, commentStatus, displayContent, translatedCommentContent, replyDtos);
  }

  private ReplyDto toReplyDto(ReplyEmbed reply, CommentDocument comment, ViewerContext context, Language targetLanguage) {
    CommentDisplayStatus replyStatus = statusCalculator.determineReplyStatus(reply, context);

    String translatedReplyContent = reply.getTranslations().stream()
        .filter(t -> t.languageCode().equals(targetLanguage.getCode()))
        .findFirst()
        .map(Translation::content)
        .orElseGet(() -> {
          if (reply.getLanguage().equals(targetLanguage)) {
            return reply.getContent();
          }
          String translatedContent = translationService.translateForRealTime(reply.getContent(), reply.getLanguage(), targetLanguage);
          applicationEventPublisher.publishEvent(new ReplyTranslationUpdatedEvent(comment.getCommentId(), reply.getReplyId(), translatedContent, targetLanguage));
          return translatedContent;
        });

    String replyContent = statusCalculator.getDisplayContent(translatedReplyContent, replyStatus);
    return ReplyDto.from(reply, replyStatus, replyContent, translatedReplyContent);
  }
}