package com.synapps.resona.translation.port;

import com.synapps.resona.entity.Language;
import java.util.List;
import java.util.Map;

public interface Translator {

  /**
   * 단일 텍스트를 특정 언어로 번역한다.
   */
  String translate(String text, Language sourceLanguage, Language targetLanguage);

  /**
   * 단일 텍스트를 여러 언어로 한 번에 번역한다.
   */
  Map<Language, String> translateToMultiple(String text, Language sourceLanguage, List<Language> targetLanguages);
}