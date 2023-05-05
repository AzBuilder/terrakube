package org.terrakube.api.plugin.streaming;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class StreamingConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }
    @Bean
    JedisConnectionFactory jedisConnectionFactory(StreamingProperties streamingProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(streamingProperties.getHostname(), streamingProperties.getPort());
        redisStandaloneConfiguration.setPassword(streamingProperties.getPassword());
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        return jedisConFactory;
    }


}
