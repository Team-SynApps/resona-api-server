package com.synapps.resona.config;


import com.synapps.resona.ResonaAPIServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = ResonaAPIServer.class)
public class FeignClientConfig {

}