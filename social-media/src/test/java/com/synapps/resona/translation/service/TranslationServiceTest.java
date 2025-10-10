
package com.synapps.resona.translation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.mock;
import com.synapps.resona.entity.Language;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.translation.port.Translator;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @InjectMocks
    private TranslationService translationService;

    @Mock
    private Translator translator;

    @Mock
    private FeedReadRepository feedReadRepository;


    @Test
    @DisplayName("실시간 번역을 성공적으로 수행한다.")
    void translateForRealTime_Success() {
        // given
        String text = "안녕하세요";
        Language sourceLanguage = Language.ko;
        Language targetLanguage = Language.en;

        when(translator.translate(text, sourceLanguage, targetLanguage)).thenReturn("Hello");

        // when
        String result = translationService.translateForRealTime(text, sourceLanguage, targetLanguage);

        // then
        assertThat(result).isEqualTo("Hello");
    }
}
