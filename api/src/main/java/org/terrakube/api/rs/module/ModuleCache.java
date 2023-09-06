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

import java.util.*;


@Slf4j
public class ModuleCache {

    private Jedis jedis;

    public ModuleCache() {
        log.warn("Init Module Cache...");

        String hostname = System.getenv("TerrakubeRedisHostname");
        String port = System.getenv("TerrakubeRedisPort");
        String password = System.getenv("TerrakubeRedisPassword");

        this.jedis = new Jedis(hostname, Integer.valueOf(port));
        this.jedis.auth(password);

        log.info("Redis connection completed...");
    }

    public List<String> getVersions(String modulePath, String source, Vcs vcs, Ssh ssh) {
        Optional<String> currentList = Optional.ofNullable(jedis.get(modulePath));
        this.jedis.expire(modulePath, 60L);
        if (currentList.isPresent()) {
            log.info("Module {} is in cache", modulePath);
            return Arrays.asList(StringUtils.split(currentList.get().toString(),"|"));
        } else {
            log.info("Module {} not in cache", modulePath);
            List<String> fromRepository = getVersionFromRepository(source, vcs, ssh);
            this.jedis.set(modulePath, StringUtils.join(fromRepository, "|"));
            this.jedis.expire(modulePath, 60L);
            return fromRepository;
        }
    }

    private List<String> getVersionFromRepository(String source, Vcs vcs, Ssh ssh) {
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

    public void setVersion(String module, List<String> versionList) {

    }

}
