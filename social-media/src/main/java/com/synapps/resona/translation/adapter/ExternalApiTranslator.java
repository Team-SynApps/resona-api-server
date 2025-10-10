package com.synapps.resona.translation.adapter;

import com.synapps.resona.entity.Language;
import com.synapps.resona.translation.port.Translator;
import com.synapps.resona.translation.dto.TranslationRequest;
import com.synapps.resona.translation.dto.TranslationResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiTranslator implements Translator {

  private final RestTemplate restTemplate;

  private final String apiUrl;

  public ExternalApiTranslator(RestTemplate restTemplate, @Value("${translation.api.url}") String apiUrl) {
    this.restTemplate = restTemplate;
    this.apiUrl = apiUrl;
  }

  @Override
  public String translate(String text, Language sourceLanguage, Language targetLanguage) {
    TranslationRequest requestPayload = new TranslationRequest(text, targetLanguage.getCode());

    TranslationResponse response = restTemplate.postForObject(apiUrl, requestPayload, TranslationResponse.class);

    return Objects.requireNonNull(response).getTranslation();
  }

  @Override
  public Map<Language, String> translateToMultiple(String text, Language sourceLanguage, List<Language> targetLanguages) {
    return targetLanguages.parallelStream()
        .collect(Collectors.toMap(
            targetLang -> targetLang,
            targetLang -> translate(text, sourceLanguage, targetLang)
        ));
  }
}