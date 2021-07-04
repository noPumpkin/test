package com.example.seckill.rabbitmq;

import com.example.seckill.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Service
public class MQSender {
    // 这里用的是RabbitTemplate发消息，也可以用AmqpTemplate，推荐使用RabbitTemplate。

    @Autowired
    AmqpTemplate amqpTemplate ;

    public void sendSeckillMessage(SeckillMessage msg) {
        String newMsg = RedisService.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, newMsg);
    }

}
