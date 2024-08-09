package org.terrakube.api.rs.module;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.terrakube.api.plugin.ssh.TerrakubeSshdSessionFactory;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.Vcs;
import org.terrakube.api.rs.vcs.VcsConnectionType;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class GitTagsCache {
    private static JedisPool jedisPool;

    private static SSLSocketFactory createTrustStoreSSLSocketFactory(String jksFile, String password) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("jks");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(jksFile);
            trustStore.load(inputStream, password.toCharArray());
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    private String getFromEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public GitTagsCache() {
        log.debug("Init Module Cache...");

        String truststorePath = System.getenv("TerrakubeRedisTruststorePath");
        String truststorePassword = System.getenv("TerrakubeRedisTruststorePassword");
        Boolean useSSL = Boolean.parseBoolean(getFromEnvOrDefault("TerrakubeRedisSSL", "false").trim())
                && truststorePath != null
                && truststorePassword != null;

        String hostname = System.getenv("TerrakubeRedisHostname");
        String username = getFromEnvOrDefault("TerrakubeRedisUsername", null);
        String port = System.getenv("TerrakubeRedisPort");
        String password = System.getenv("TerrakubeRedisPassword");
        String maxTotal = getFromEnvOrDefault("ModuleCacheMaxTotal", "128");
        String maxIdle = getFromEnvOrDefault("ModuleCacheMaxIdle", "128");
        String minIdle = getFromEnvOrDefault("ModuleCacheMinIdle", "64");
        String timeout = getFromEnvOrDefault("ModuleCacheTimeout", "600000");
        String schedule = getFromEnvOrDefault("ModuleCacheSchedule", "0 */3 * ? * *");

        try {
            SSLSocketFactory sslSocketFactory = null;
            if (useSSL) {
                sslSocketFactory = createTrustStoreSSLSocketFactory(truststorePath, truststorePassword);
            }

            synchronized (this) {
                if (jedisPool == null) {
                    if (hostname != null && port != null && password != null) {
                        log.warn("Module Config: MaxTotal {} MaxIdle {} MinIdle {} Timeout {} Schedule {}", maxTotal,
                                maxIdle, minIdle, timeout, schedule);
                        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                        jedisPoolConfig.setMaxTotal(Integer.valueOf(maxTotal));
                        jedisPoolConfig.setMaxIdle(Integer.valueOf(maxIdle));
                        jedisPoolConfig.setMinIdle(Integer.valueOf(minIdle));

                        if (useSSL && username != null) {
                            log.warn("Connecting Redis using hostname, port, username, password and sslSocketFactory");
                            jedisPool = new JedisPool(jedisPoolConfig, hostname, Integer.valueOf(port),
                                    Integer.valueOf(timeout), Integer.valueOf(timeout), username, password, 0, null,
                                    true, sslSocketFactory, null, null);
                        } else if (username != null) {
                            log.warn("Connecting Redis using hostname, port, username, password with SSL enabled",
                                    username);
                            jedisPool = new JedisPool(jedisPoolConfig, hostname, Integer.valueOf(port),
                                    Integer.valueOf(timeout), username, password, true);
                        } else {
                            log.warn("Connecting Default Redis using hostname, port and password");
                            jedisPool = new JedisPool(jedisPoolConfig, hostname, Integer.valueOf(port),
                                    Integer.valueOf(timeout), password);
                        }
                    }
                    log.info("Redis connection completed...");
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private Jedis getJedisConnection() {
        return jedisPool.getResource();
    }

    public List<String> getVersions(String modulePath, String tagPrefix, String source, Vcs vcs, Ssh ssh,
            GitHubAppToken gitHubAppToken) {
        Jedis connection;
        String cacheFromRedis = null;
        if (jedisPool != null) {
            connection = getJedisConnection();
            cacheFromRedis = connection.get(modulePath);
            jedisPool.returnResource(connection);
        }
        Optional<String> currentList = Optional.ofNullable((jedisPool != null) ? cacheFromRedis : null);
        if (currentList.isPresent()) {
            log.info("Module {} is in cache", modulePath);
            return Arrays.asList(StringUtils.split(currentList.get().toString(), "|"));
        } else {
            log.info("Module {} is not in cache, adding to cache (this should not happen...)", modulePath);
            List<String> fromRepository = getVersionFromRepository(source, tagPrefix, vcs, ssh, gitHubAppToken);
            if (jedisPool != null) {
                connection = getJedisConnection();
                connection.set(modulePath, StringUtils.join(fromRepository, "|"));
                jedisPool.returnResource(connection);
            }

            return fromRepository;
        }
    }

    public List<String> getVersionFromRepository(String source, String tagPrefix, Vcs vcs, Ssh ssh,
            GitHubAppToken gitHubAppToken) {
        List<String> versionList = new ArrayList<>();
        try {
            CredentialsProvider credentialsProvider = null;
            TransportConfigCallback transportConfigCallback = null;
            Map<String, Ref> tags = null;
            if (vcs != null) {
                log.info("vcs using {}", vcs.getVcsType().toString());
                switch (vcs.getVcsType()) {
                    case GITHUB:
                        if (vcs.getConnectionType() == VcsConnectionType.OAUTH) {
                            credentialsProvider = new UsernamePasswordCredentialsProvider(vcs.getAccessToken(), "");
                        } else {
                            credentialsProvider = new UsernamePasswordCredentialsProvider("x-access-token",
                                    gitHubAppToken.getToken());
                        }
                        break;
                    case BITBUCKET:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("x-token-auth",
                                vcs.getAccessToken());
                        break;
                    case GITLAB:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", vcs.getAccessToken());
                        break;
                    case AZURE_DEVOPS:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("dummy", vcs.getAccessToken());
                        break;
                    default:
                        credentialsProvider = null;
                        break;
                }

                tags = Git.lsRemoteRepository()
                        .setTags(true)
                        .setRemote(source)
                        .setCredentialsProvider(credentialsProvider)
                        .callAsMap();
            }

            if (ssh != null) {
                log.info("vcs using ssh {}", ssh.getId());

                transportConfigCallback = transport -> {
                    if (transport instanceof SshTransport) {
                        if (transport instanceof SshTransport) {
                            TerrakubeSshdSessionFactory terrakubeSshdSessionFactory = TerrakubeSshdSessionFactory
                                    .builder()
                                    .sshId(ssh.getId().toString())
                                    .sshFileName(ssh.getSshType().getFileName())
                                    .privateKey(ssh.getPrivateKey())
                                    .build();
                            ((SshTransport) transport)
                                    .setSshSessionFactory(terrakubeSshdSessionFactory.getSshdSessionFactory());
                        }
                    }
                };

                tags = Git.lsRemoteRepository()
                        .setTags(true)
                        .setRemote(source)
                        .setTransportConfigCallback(transportConfigCallback)
                        .callAsMap();
            }

            if (ssh == null && vcs == null) {
                tags = Git.lsRemoteRepository()
                        .setTags(true)
                        .setRemote(source)
                        .callAsMap();
            }

            tags.forEach((key, value) -> {
                String originalTag = key.replace("refs/tags/", "");
                if (tagPrefix == null) {
                    versionList.add(originalTag);
                } else if (originalTag.startsWith(tagPrefix)) {
                    versionList.add(originalTag.replace(tagPrefix, ""));
                }
            });
        } catch (GitAPIException e) {
            log.error(e.getMessage());
        }
        return versionList;
    }

    public void setVersions(String modulePath, List<String> moduleVersions) {
        if (jedisPool != null) {
            Jedis connection = getJedisConnection();
            connection.set(modulePath, StringUtils.join(moduleVersions, "|"));
            jedisPool.returnResource(connection);
        }
    }

}
