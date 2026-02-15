package com.example.wms.consumer;

import com.example.wms.entity.Notice;
import com.example.wms.service.NoticeService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class NoticeConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(NoticeConsumer.class);
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String UNREAD_COUNT_KEY_PREFIX = "notice:unread:";
    private static final long CACHE_EXPIRE_TIME = 30;
    
    @RabbitListener(queues = "notice.queue")
    public void handleNotice(Notice notice, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            logger.info("收到通知消息: userId={}, title={}", notice.getUserId(), notice.getTitle());
            
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + notice.getUserId();
            
            redisTemplate.opsForValue().increment(cacheKey);
            redisTemplate.expire(cacheKey, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            
            channel.basicAck(deliveryTag, false);
            
            logger.info("通知处理成功: userId={}, title={}", notice.getUserId(), notice.getTitle());
        } catch (Exception e) {
            logger.error("处理通知消息失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                logger.error("拒绝消息失败: {}", ex.getMessage(), ex);
            }
        }
    }
}