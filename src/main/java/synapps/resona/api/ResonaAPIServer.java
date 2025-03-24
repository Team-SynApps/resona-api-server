package synapps.resona.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.global.properties.CorsProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({
        CorsProperties.class,
        AppProperties.class
})
public class ResonaAPIServer {
//    static {
//        // .env 파일을 로드해서 각 항목을 시스템 프로퍼티로 등록
//        Dotenv dotenv = Dotenv.configure()
//                .directory("./")
//                .ignoreIfMissing() // 파일이 없으면 무시하도록 설정 -> 실제 운영 환경에서는 해당되지 않기 때문
//                .load();
//        dotenv.entries().forEach(entry ->
//                System.setProperty(entry.getKey(), entry.getValue())
//        );
//    }

    public static void main(String[] args) {
        SpringApplication.run(ResonaAPIServer.class, args);
    }

}
