package com.synapps.resona;

import com.synapps.resona.properties.RedisTtlProperties;
import com.synapps.resona.retrieval.config.FeedRetrievalProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "com.synapps.resona")
//@EnableConfigurationProperties({RedisTtlProperties.class, FeedRetrievalProperties.class})
@EnableJpaAuditing
//@EntityScan(basePackages = "com.synapps.resona")
//@EnableJpaRepositories(basePackages = "com.synapps.resona")
public class TestConfiguration {
}
