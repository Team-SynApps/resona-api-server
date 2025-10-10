package com.synapps.resona.translation.service;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.comment.CommentTranslation;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.entity.reply.ReplyTranslation;
import com.synapps.resona.comment.command.repository.CommentTranslationRepository;
import com.synapps.resona.comment.command.repository.ReplyTranslationRepository;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.event.CommentTranslationsCreatedEvent;
import com.synapps.resona.comment.event.ReplyTranslationsCreatedEvent;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.FeedTranslationRepository;
import com.synapps.resona.feed.event.FeedTranslationsCreatedEvent;
import com.synapps.resona.translation.port.Translator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TranslationService {

  private final Translator translator;
  private final ApplicationEventPublisher eventPublisher;

  private static final List<Language> targetLanguages = List.of(Language.en, Language.zh_CN, Language.hi, Language.bn, Language.es, Language.fr, Language.ja, Language.ko);

  public String translateForRealTime(String text, Language sourceLanguage, Language targetLanguage) {
    return translator.translate(text, sourceLanguage, targetLanguage);
  }

  public List<Translation> translateToTargetLanguages(String content, Language language) {
    Map<Language, String> translatedResults = translator.translateToMultiple(content, language, targetLanguages);
    return translatedResults.entrySet().stream()
        .map(entry -> new Translation(entry.getKey().getCode(), entry.getValue()))
        .toList();
  }


  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param feedId
   * @param originalText
   * @param sourceLanguage
   */
  public void preTranslateFeed(Long feedId, String originalText, Language sourceLanguage) {
    Map<Language, String> translatedResults = translator.translateToMultiple(originalText, sourceLanguage, targetLanguages);
    eventPublisher.publishEvent(new FeedTranslationsCreatedEvent(feedId, translatedResults));
  }

  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param commentId
   * @param originalText
   * @param sourceLanguage
   */
  public void preTranslateComment(Long commentId, String originalText, Language sourceLanguage) {
    Map<Language, String> translatedResults = translator.translateToMultiple(originalText, sourceLanguage, targetLanguages);
    eventPublisher.publishEvent(new CommentTranslationsCreatedEvent(commentId, translatedResults));
  }

  /**
   * MongoDB replica set을 사용하지 않는 한 아직 사용할 수 없음. race condition 발생
   * - 스프링 트랜잭션이 보장이 되지 않음
   * - MongoDB replica set을 사용하는 경우, 스프링 트랜잭션에 적용 가능
   * @param commentId
   * @param replyId
   * @param originalText
   * @param sourceLanguage
   */
  public void preTranslateReply(Long commentId, Long replyId, String originalText, Language sourceLanguage) {
    Map<Language, String> translatedResults = translator.translateToMultiple(originalText, sourceLanguage, targetLanguages);
    eventPublisher.publishEvent(new ReplyTranslationsCreatedEvent(commentId, replyId, translatedResults));
  }

}