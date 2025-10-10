package com.synapps.resona.config.database;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  // Exchange 이름 정의
  public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
  public static final String PUSH_QUEUE_NAME = "chat.push_notification.queue";
  public static final String ROUTING_KEY_PATTERN = "room.#";

  /**
   * - TopicExchange Bean 생성
   * - chat.exchange라는 이름으로 생성
   */
  @Bean
  public TopicExchange chatExchange() {
    return new TopicExchange(CHAT_EXCHANGE_NAME);
  }

  /**
   * 푸시 알림 처리를 위한 전용 큐(Queue)
   * durable=true로 설정하여 RabbitMQ 서버가 재시작되어도 큐가 유지
   */
  @Bean
  public Queue pushQueue() {
    return new Queue(PUSH_QUEUE_NAME, true);
  }

  /**
   * Exchange와 푸시 알림 큐를 바인딩
   * "room."으로 시작하는 모든 라우팅 키를 가진 메시지가 이 큐로 전달
   */
  @Bean
  public Binding pushBinding(Queue pushQueue, TopicExchange chatExchange) {
    return BindingBuilder.bind(pushQueue).to(chatExchange).with(ROUTING_KEY_PATTERN);
  }

  /**
   * - RabbitTemplate이 메시지를 전송할 때 사용할 MessageConverter를 설정
   * - 여기서는 Jackson2JsonMessageConverter를 사용하여 객체를 JSON 형태로 직렬화
   * - 다양한 컨슈머(Consumer)가 메시지 포맷에 구애받지 않고 쉽게 파싱할 수 있음
   */
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * RabbitMQ 메시지 발행(publish) 및 수신(receive)을 위한 기본 템플릿 설정
   * 위에서 정의한 jsonMessageConverter를 사용하도록 설정
   * DTO 객체를 보낼 때 자동으로 JSON으로 변환
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }
}