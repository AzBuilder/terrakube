package org.terrakube.executor.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisAutoConfiguration {

    @Bean
    SSLSocketFactory sslSocketFactory(RedisProperties properties) throws Exception {
        if (!properties.isSsl())
            return SSLContext.getDefault().getSocketFactory();

        KeyStore jksTrustStore = KeyStore.getInstance("jks");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(properties.getTruststorePath());
            char[] truststorePassword = properties.getTruststorePassword().toCharArray();
            jksTrustStore.load(inputStream, truststorePassword);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        TrustManagerFactory managerFactory = TrustManagerFactory.getInstance("PKIX");
        managerFactory.init(jksTrustStore);
        TrustManager[] trustManagers = managerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties, SSLSocketFactory sslSocketFactory) {
        String hostname = redisProperties.getHostname();
        int port = redisProperties.getPort();
        String username = redisProperties.getUsername();
        String password = redisProperties.getPassword();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                hostname, port);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setUsername(username);

        JedisConnectionFactory jedisConFactory;

        if (redisProperties.isSsl()) {
            JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration
                    .builder()
                    .useSsl()
                    .sslSocketFactory(sslSocketFactory)
                    .build();

            jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
        } else {
            jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        }

        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }
}
