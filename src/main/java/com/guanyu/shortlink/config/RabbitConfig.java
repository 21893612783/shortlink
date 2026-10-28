package com.guanyu.shortlink.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String STAT_EXCHANGE = "shortlink.stat.exchange";
    public static final String STAT_QUEUE = "shortlink.stat.queue";
    public static final String STAT_ROUTING_KEY = "shortlink.stat.key";

    @Bean
    public DirectExchange statExchange() {
        return new DirectExchange(STAT_EXCHANGE, true, false);
    }

    @Bean
    public Queue statQueue() {
        return QueueBuilder.durable(STAT_QUEUE).build();
    }

    @Bean
    public Binding statBinding() {
        return BindingBuilder.bind(statQueue())
                .to(statExchange())
                .with(STAT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}