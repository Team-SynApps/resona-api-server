package com.synapps.atch;

import com.synapps.atch.global.properties.AppProperties;
import com.synapps.atch.global.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
public class AtchApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtchApplication.class, args);
	}

}
