package com.guanyu.shortlink.stat;

import com.guanyu.shortlink.config.RabbitConfig;
import com.guanyu.shortlink.mapper.LinkStatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessStatConsumer {

    private final LinkStatMapper linkStatMapper;
    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = RabbitConfig.STAT_QUEUE)
    public void onMessage(AccessMessage msg) {
        String code = msg.getCode();
        LocalDate today = LocalDate.now();

        linkStatMapper.incrPv(code, today);

        String uvKey = "shortlink:uv:" + code + ":" + today;
        Long added = redisTemplate.opsForSet().add(uvKey, msg.getIp());
        if (added != null && added > 0) {
            linkStatMapper.incrUv(code, today);
        }
        redisTemplate.expire(uvKey, Duration.ofDays(2));

        log.info("统计 code={} pv+1 ip={}", code, msg.getIp());
    }
}