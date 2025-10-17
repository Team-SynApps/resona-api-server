package com.synapps.resona.report;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "com.synapps.resona")
//@EnableConfigurationProperties({RedisTtlProperties.class, FeedRetrievalProperties.class})
@EnableJpaAuditing
//@EntityScan(basePackages = "com.synapps.resona")
//@EnableJpaRepositories(basePackages = "com.synapps.resona")
public class TestConfiguration {
}
