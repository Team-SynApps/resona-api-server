package com.synapps.resona.translation.service;

import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.entity.Language;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.translation.port.Translator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

  private final Translator translator;
  private final FeedReadRepository feedReadRepository;
  // private final CommentReadRepository commentReadRepository;

  @Async
  public void preTranslateAndSave(Long feedId, String originalText, Language sourceLanguage) {
    // TODO: 시스템에서 지원하는 주요 언어 목록을 가져온다.
    List<Language> targetLanguages = List.of(Language.ENGLISH, Language.JAPANESE, Language.CHINESE_SIMPLIFIED);

    // 외부 API 호출
    Map<Language, String> translatedResults = translator.translateToMultiple(originalText, sourceLanguage, targetLanguages);

    // MongoDB에서 FeedDocument를 찾아 번역본을 업데이트
    feedReadRepository.findByFeedId(feedId).ifPresent(feedDoc -> {
      for (Language language : translatedResults.keySet()) {
        feedDoc.addTranslation(Translation.of(language.toString(), translatedResults.get(language)));
      }
      feedReadRepository.save(feedDoc);
    });
  }

  public String translateForRealTime(String text, Language sourceLanguage, Language targetLanguage) {
    // TODO: 동일한 요청에 대한 결과를 캐싱하는 로직 추가하면 좋음 (e.g., Redis)

    return translator.translate(text, sourceLanguage, targetLanguage);
  }
}