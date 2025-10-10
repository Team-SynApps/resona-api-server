package com.synapps.resona.config.database;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoAuditing
@EnableMongoRepositories(
    basePackages = "com.synapps.resona",
    includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MongoDBRepository.class)
)
public class MongoConfig {

  private final MongoMappingContext mongoMappingContext;

  @Bean
  public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
      MongoMappingContext mongoMappingContext) {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    return converter;
  }
}