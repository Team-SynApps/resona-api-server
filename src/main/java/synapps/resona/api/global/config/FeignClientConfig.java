package synapps.resona.api.global.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import synapps.resona.api.ResonaAPIServer;

@Configuration
@EnableFeignClients(basePackageClasses = ResonaAPIServer.class)
public class FeignClientConfig {

}

