package com.synapps.resona.config.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);  // 기본 스레드 풀 크기
    executor.setMaxPoolSize(80);   // 최대 스레드 풀 크기
    executor.setQueueCapacity(50); // 대기 큐 크기
    executor.setKeepAliveSeconds(60); // 추가 스레드 생존 시간
    return executor;
  }
}
