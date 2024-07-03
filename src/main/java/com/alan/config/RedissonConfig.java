package com.alan.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Alan
 * @Date 2024/7/3 21:17
 * @Description Redisson配置类
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    private String password;
    private int rdatabase;
    /**
     * 创建Redisson客户端
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(address).setPassword(password).setDatabase(rdatabase);
        // 2. 创建 Redisson 实例
        return Redisson.create(config);
    }
}
