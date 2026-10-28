package com.guanyu.shortlink.stat;

import com.guanyu.shortlink.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessStatProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(String code, String ip) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.STAT_EXCHANGE,
                    RabbitConfig.STAT_ROUTING_KEY,
                    new AccessMessage(code, ip, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("投递统计消息失败 code={}", code, e);
        }
    }
}