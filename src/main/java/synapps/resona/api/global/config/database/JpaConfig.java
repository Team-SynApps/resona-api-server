package synapps.resona.api.global.config.database;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;

@Configuration
//@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "synapps.resona.api",
    includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MySQLRepository.class)
)
public class JpaConfig {

}