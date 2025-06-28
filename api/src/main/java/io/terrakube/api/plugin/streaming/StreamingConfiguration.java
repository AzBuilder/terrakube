package io.terrakube.api.plugin.streaming;

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
public class StreamingConfiguration {

    @Bean
    SSLSocketFactory sslSocketFactory(StreamingProperties props) throws Exception {
        if (!props.isSsl()) {
            return SSLContext.getDefault().getSocketFactory();
        }
        KeyStore trustStore = KeyStore.getInstance("jks");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(props.getTruststorePath());
            trustStore.load(inputStream, props.getTruststorePassword().toCharArray());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory(StreamingProperties props, SSLSocketFactory sslSocketFactory) {
        log.info("Redis Configuration=> User: {}, Hostname: {}, Port: {}, Ssl: {}",
                (props.getUsername() != null && !props.getUsername().isEmpty()) ? props.getUsername(): "username is null",
                props.getHostname(),
                props.getPort(),
                props.isSsl()
        );

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                props.getHostname(), props.getPort());
        redisStandaloneConfiguration.setPassword(props.getPassword());

        if( props.getUsername() != null && !props.getUsername().isEmpty()) {
            log.info("Setting redis connection username");
            redisStandaloneConfiguration.setUsername(props.getUsername());
        } else {
            log.info("Redis connection is not using username parameter");
        }

        JedisConnectionFactory jedisConFactory;

        if (props.isSsl()) {
            log.info("Setup Redis connection using SSL");
            JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().useSsl()
                    .sslSocketFactory(sslSocketFactory).build();

            jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration,
                    jedisClientConfiguration);
        } else {
            log.info("Using default Redis connection");
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
