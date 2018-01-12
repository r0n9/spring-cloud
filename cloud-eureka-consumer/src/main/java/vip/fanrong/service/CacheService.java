package vip.fanrong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

    private RedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private ZSetOperations<String, String> zSetOperations;

    @Autowired
    public CacheService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public String getFromRedis(String key) {
        return valueOperations.get(key);
    }

    public void setToRedis(String key, String value) {
        valueOperations.set(key, value);
    }

    public void delFromRedis(String key) {
        redisTemplate.delete(key);
    }

    public void incrScore(String key, String member, double score) {
        zSetOperations.incrementScore(key, member, score);
    }

}
