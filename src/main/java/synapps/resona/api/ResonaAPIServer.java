package synapps.resona.api;

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

    public static void main(String[] args) {
        SpringApplication.run(ResonaAPIServer.class, args);
    }

}
