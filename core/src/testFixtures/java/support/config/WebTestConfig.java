package support.config;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class WebTestConfig {

  @Bean
  public MockMvcBuilderCustomizer utf8CharacterEncodingFilter() {
    return builder -> builder.addFilters(new CharacterEncodingFilter("UTF-8", true));
  }
}