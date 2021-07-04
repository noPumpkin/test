package com.example.seckill.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String SECKILL_QUEUE = "seckill.queue";

    @Bean
    public Queue secKillQueue(){
        return new Queue(SECKILL_QUEUE,true);
    }
}
