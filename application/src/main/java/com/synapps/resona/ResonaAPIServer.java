package com.synapps.resona;

import com.synapps.resona.properties.AppProperties;
import com.synapps.resona.properties.CorsProperties;
import com.synapps.resona.properties.RedisTtlProperties;
import io.github.cdimascio.dotenv.Dotenv;
import com.synapps.resona.config.MemberProperties;
import com.synapps.resona.retrieval.config.FeedRetrievalProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties({
    CorsProperties.class,
    AppProperties.class,
    RedisTtlProperties.class,
    MemberProperties.class,
    FeedRetrievalProperties.class
})
public class ResonaAPIServer {
  // 로컬 시스템 프로퍼티 등록용
  static {
    Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing() // 파일이 없으면 무시하도록 설정 -> 실제 운영 환경에서는 해당되지 않음
        .load();
    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );
  }

  public static void main(String[] args) {
    SpringApplication.run(ResonaAPIServer.class, args);
  }

}
