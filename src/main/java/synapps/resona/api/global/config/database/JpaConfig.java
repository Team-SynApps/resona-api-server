package synapps.resona.api.global.config.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "synapps.resona.api.mysql")
public class JpaConfig {
}