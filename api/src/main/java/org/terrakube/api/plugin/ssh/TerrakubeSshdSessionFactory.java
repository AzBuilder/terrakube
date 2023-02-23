package org.terrakube.api.plugin.ssh;


import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.ServerKeyDatabase;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

@Builder
@Slf4j
public class TerrakubeSshdSessionFactory {

    private static String SSH_FOLDER = "%s/.terraform-spring-boot/ssh/%s/%s";

    private String sshId;
    private String sshFileName;
    private String privateKey;

    public SshdSessionFactory getSshdSessionFactory() {
        File sshDir = generateSshFolder();
        SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setServerKeyDatabase((h, s) -> new ServerKeyDatabase() {

                    @Override
                    public List<PublicKey> lookup(String connectAddress,
                                                  InetSocketAddress remoteAddress,
                                                  Configuration config) {
                        return Collections.emptyList();
                    }

                    @Override
                    public boolean accept(String connectAddress,
                                          InetSocketAddress remoteAddress,
                                          PublicKey serverKey, Configuration config,
                                          CredentialsProvider provider) {
                        return true;
                    }

                })
                .setSshDirectory(sshDir)
                .build(new JGitKeyCache());

        return sshdSessionFactory;
    }

    private File generateSshFolder() {
        String sshFilePath = String.format(SSH_FOLDER, FileUtils.getUserDirectoryPath(), this.sshId, this.sshFileName);
        File sshFile = new File(sshFilePath);
        try {
            if(!sshFile.exists()) {
                log.info("Creating new SSH folder for {}", this.sshId);
                FileUtils.forceMkdirParent(sshFile);
                FileUtils.writeStringToFile(sshFile, this.privateKey, Charset.defaultCharset().toString());
            } else {
                log.info("SSH folder for {} already exists", this.sshId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sshFile.getParentFile();
    }
}
