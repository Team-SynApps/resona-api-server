package com.synapps.resona.translation.adapter;

import com.synapps.resona.entity.Language;
import com.synapps.resona.translation.port.Translator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiTranslator implements Translator {

  // private final RestTemplate restTemplate;

  @Override
  public String translate(String text, Language sourceLanguage, Language targetLanguage) {
    // TODO: 외부 번역 API를 호출하는 실제 로직 구현
    return "Translated text for " + targetLanguage.name();
  }

  @Override
  public Map<Language, String> translateToMultiple(String text, Language sourceLanguage, List<Language> targetLanguages) {
    // TODO: 외부 API가 여러 언어 동시 번역을 지원하면 한 번에, 아니면 반복 호출
    return Map.of(Language.ENGLISH, "Translated text for ENGLISH");
  }
}