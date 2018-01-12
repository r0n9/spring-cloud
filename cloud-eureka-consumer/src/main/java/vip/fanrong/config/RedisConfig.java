package vip.fanrong.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class RedisConfig {

    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisPoolConfig getRedisConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory getConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig poolConfig = getRedisConfig();

        poolConfig.setMaxWaitMillis(Long.parseLong(env.getProperty("spring.redis.pool.max-wait").trim()));
        poolConfig.setMaxIdle(Integer.parseInt(env.getProperty("spring.redis.pool.max-idle").trim()));
        poolConfig.setMinIdle(Integer.parseInt(env.getProperty("spring.redis.pool.min-idle").trim()));

        factory.setPoolConfig(poolConfig);

        factory.setHostName(env.getProperty("spring.redis.host").trim());
        factory.setPort(Integer.parseInt(env.getProperty("spring.redis.port").trim()));
        factory.setPassword(env.getProperty("spring.redis.password").trim());
        factory.setDatabase(Integer.parseInt(env.getProperty("spring.redis.database").trim()));
        factory.setUsePool(true);
        factory.setTimeout(Integer.parseInt(env.getProperty("spring.redis.timeout").trim()));

        return factory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate initRedisTemplate() {

        RedisTemplate redisTemplate = new RedisTemplate();
        RedisSerializer StringSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(getConnectionFactory());
        redisTemplate.setDefaultSerializer(StringSerializer);
        redisTemplate.setKeySerializer(StringSerializer);
        redisTemplate.setValueSerializer(StringSerializer);
        redisTemplate.setHashKeySerializer(StringSerializer);
        redisTemplate.setHashValueSerializer(StringSerializer);
        return redisTemplate;
    }
}  
