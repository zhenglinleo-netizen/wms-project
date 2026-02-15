package com.example.wms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理工具类
 * 用于简化Redis缓存的操作
 */
@Component
public class CacheManager {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 设置缓存
     */
    public <T> void set(String key, T value, int expireTime) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, jsonValue, expireTime, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取缓存
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String jsonValue = stringRedisTemplate.opsForValue().get(key);
            if (jsonValue != null) {
                return objectMapper.readValue(jsonValue, clazz);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 删除缓存
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
    
    /**
     * 批量删除缓存
     */
    public void deletePattern(String pattern) {
        stringRedisTemplate.delete(stringRedisTemplate.keys(pattern));
    }
    
    /**
     * 检查缓存是否存在
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
    
    /**
     * 延长缓存过期时间
     */
    public void expire(String key, int expireTime) {
        stringRedisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
    }
}
