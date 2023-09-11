package org.terrakube.api.rs.module;

import lombok.extern.slf4j.Slf4j;
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
import org.terrakube.api.rs.vcs.Vcs;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;


@Slf4j
public class GitTagsCache {
    private static JedisPool jedisPool;

    public GitTagsCache() {
        log.warn("Init Module Cache...");

        String hostname = System.getenv("TerrakubeRedisHostname");
        String port = System.getenv("TerrakubeRedisPort");
        String password = System.getenv("TerrakubeRedisPassword");
        log.info(password);
        String maxTotal = System.getenv("ModuleCacheMaxTotal") != null ? System.getenv("ModuleCacheMaxTotal"): "128";
        String maxIdle = System.getenv("ModuleCacheMaxIdle") != null ? System.getenv("ModuleCacheMaxIdle"): "128";
        String minIdle = System.getenv("ModuleCacheMinIdle") != null ? System.getenv("ModuleCacheMinIdle"): "64";
        String timeout = System.getenv("ModuleCacheTimeout") != null ? System.getenv("ModuleCacheTimeout"): "600000";
        String schedule = System.getenv("ModuleCacheSchedule") != null ? System.getenv("ModuleCacheSchedule"): "0 */3 * ? * *";

        synchronized (this) {
            if (jedisPool == null) {
                if (hostname != null && port != null && password != null) {
                    log.warn("Module Config: MaxTotal {} MaxIdle {} MinIdle {} Timeout {} Schedule {}", maxTotal, maxIdle, minIdle, timeout, schedule);
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(Integer.valueOf(maxTotal));
                    jedisPoolConfig.setMaxIdle(Integer.valueOf(maxIdle));
                    jedisPoolConfig.setMinIdle(Integer.valueOf(minIdle));
                    jedisPool = new JedisPool(jedisPoolConfig, hostname, Integer.valueOf(port), timeout, password);
                    log.info("Redis connection completed...");
                }
            }
        }

    }

    private Jedis getJedisConnection() {
        return jedisPool.getResource();
    }

    public List<String> getVersions(String modulePath, String source, Vcs vcs, Ssh ssh) {
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
            List<String> fromRepository = getVersionFromRepository(source, vcs, ssh);
            if (jedisPool != null) {
                connection = getJedisConnection();
                connection.set(modulePath, StringUtils.join(fromRepository, "|"));
                jedisPool.returnResource(connection);
            }

            return fromRepository;
        }
    }

    public List<String> getVersionFromRepository(String source, Vcs vcs, Ssh ssh) {
        List<String> versionList = new ArrayList<>();
        try {
            CredentialsProvider credentialsProvider = null;
            TransportConfigCallback transportConfigCallback = null;
            Map<String, Ref> tags = null;
            if (vcs != null) {
                log.info("vcs using {}", vcs.getVcsType().toString());
                switch (vcs.getVcsType()) {
                    case GITHUB:
                        credentialsProvider = new UsernamePasswordCredentialsProvider(vcs.getAccessToken(), "");
                        break;
                    case BITBUCKET:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("x-token-auth", vcs.getAccessToken());
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
                            ((SshTransport) transport).setSshSessionFactory(terrakubeSshdSessionFactory.getSshdSessionFactory());
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
                versionList.add(key.replace("refs/tags/", ""));
            });
        } catch (GitAPIException e) {
            log.error(e.getMessage());
        }
        return versionList;
    }

    public void setVersions(String modulePath, List<String> moduleVersions) {
        log.info("Updating module index for {}", modulePath);
        if (jedisPool != null) {
            Jedis connection = getJedisConnection();
            connection.set(modulePath, StringUtils.join(moduleVersions, "|"));
            jedisPool.returnResource(connection);
        }
    }

}
