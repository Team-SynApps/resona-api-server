package com.synapps.resona.config.database;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "com.synapps.resona",
    includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MySQLRepository.class)
)
public class JpaConfig {

}