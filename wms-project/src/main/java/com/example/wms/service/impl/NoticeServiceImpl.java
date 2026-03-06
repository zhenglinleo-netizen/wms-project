package com.example.wms.service.impl;

import com.example.wms.entity.Notice;
import com.example.wms.mapper.NoticeMapper;
import com.example.wms.service.NoticeService;
import com.example.wms.service.UserService;
import com.example.wms.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private UserService userService;
    
    private static final String NOTICE_QUEUE = "notice.queue";
    private static final String UNREAD_COUNT_KEY_PREFIX = "notice:unread:";
    private static final long CACHE_EXPIRE_TIME = 30;
    
    @Override
    public int createNotice(Notice notice) {
        return noticeMapper.insert(notice);
    }
    
    @Override
    public int updateNotice(Notice notice) {
        return noticeMapper.updateById(notice);
    }
    
    @Override
    public Notice getNoticeById(Long id) {
        return noticeMapper.selectById(id);
    }
    
    @Override
    public List<Notice> getNoticesByUserId(Long userId, Integer limit) {
        return noticeMapper.selectByUserId(userId, limit);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        if (redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            
            String cachedCount = redisTemplate.opsForValue().get(cacheKey);
            if (cachedCount != null) {
                return Integer.parseInt(cachedCount);
            }
        }
        
        int count = noticeMapper.countUnreadByUserId(userId);
        
        if (redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(count), CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        
        return count;
    }
    
    @Override
    public int markAsRead(Long userId, Long noticeId) {
        int result = noticeMapper.markAsRead(userId, noticeId);
        
        if (result > 0 && redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            redisTemplate.opsForValue().decrement(cacheKey);
        }
        
        return result;
    }
    
    @Override
    public int markAllAsRead(Long userId) {
        int result = noticeMapper.markAllAsRead(userId);
        
        if (result > 0 && redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(cacheKey, "0", CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        
        return result;
    }
    
    @Override
    public int deleteNotice(Long id) {
        return noticeMapper.deleteById(id);
    }
    
    @Override
    public void sendNoticeToQueue(Notice notice) {
        rabbitTemplate.convertAndSend(NOTICE_QUEUE, notice);
    }

    @Override
    public void sendNoticeToAdmins(String title, String content, String type, Long relatedId) {
        List<User> admins = userService.getAdmins();
        
        for (User admin : admins) {
            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setType(type);
            notice.setUserId(admin.getId());
            notice.setRelatedId(relatedId);
            notice.setIsRead(0);
            
            sendNoticeToQueue(notice);
        }
    }
}