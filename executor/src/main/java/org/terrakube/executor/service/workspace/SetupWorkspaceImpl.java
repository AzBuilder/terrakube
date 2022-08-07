package org.terrakube.executor.service.workspace;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.ServerKeyDatabase;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;
import org.terrakube.executor.service.mode.TerraformJob;
import org.terrakube.executor.service.workspace.registry.SetupPrivateRegistry;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SetupWorkspaceImpl implements SetupWorkspace {

    private static final String EXECUTOR_DIRECTORY = "/.terraform-spring-boot/executor/";
    private static final String SSH_DIRECTORY = "%s/.terraform-spring-boot/ssh/executor/%s/%s/%s";
    private static final String SSH_EXECUTOR_JOB = "%s/.terraform-spring-boot/ssh/executor/%s";

    OkHttpClient httpClient;
    SetupPrivateRegistry setupPrivateRegistry;
    boolean enableRegistrySecurity;

    public SetupWorkspaceImpl(OkHttpClient httpClient, SetupPrivateRegistry setupPrivateRegistry, @Value("${org.terrakube.client.enableSecurity}") boolean enableRegistrySecurity) {
        this.httpClient = httpClient;
        this.setupPrivateRegistry = setupPrivateRegistry;
        this.enableRegistrySecurity = enableRegistrySecurity;
    }

    @Override
    public File prepareWorkspace(TerraformJob terraformJob) {
        File workspaceFolder = null;
        try {
            workspaceFolder = setupWorkspaceDirectory(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
            downloadWorkspace(workspaceFolder, terraformJob.getSource(), terraformJob.getBranch(), terraformJob.getVcsType(), terraformJob.getAccessToken(), terraformJob.getJobId());
            if (enableRegistrySecurity)
                setupPrivateRegistry.addCredentials(workspaceFolder);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return workspaceFolder;
    }

    private File setupWorkspaceDirectory(String organizationId, String workspaceId) throws IOException {
        String userHomeDirectory = FileUtils.getUserDirectoryPath();
        log.info("User Home Directory: {}", userHomeDirectory);

        String executorPath = userHomeDirectory.concat(
                FilenameUtils.separatorsToSystem(
                        EXECUTOR_DIRECTORY + organizationId + "/" + workspaceId
                ));
        File executorFolder = new File(executorPath);
        FileUtils.forceMkdir(executorFolder);
        FileUtils.cleanDirectory(executorFolder);
        log.info("Workspace directory: {}", executorPath);
        return executorFolder;
    }

    private void downloadWorkspace(File workspaceFolder, String source, String branch, String vcsType, String accessToken, String jobId) throws IOException {
        try {
            if (vcsType.startsWith("SSH")) {
                Git.cloneRepository()
                        .setURI(source)
                        .setDirectory(workspaceFolder)
                        .setBranch(branch)
                        .setTransportConfigCallback(transport -> {
                            ((SshTransport) transport).setSshSessionFactory(getSshdSessionFactory(vcsType, accessToken, jobId));
                        })
                        .call();
                FileUtils.deleteDirectory(new File(String.format(SSH_EXECUTOR_JOB, FileUtils.getUserDirectoryPath(), jobId)));
            } else {
                Git.cloneRepository()
                        .setURI(source)
                        .setDirectory(workspaceFolder)
                        .setCredentialsProvider(setupCredentials(vcsType, accessToken))
                        .setBranch(branch)
                        .call();
            }
        } catch (GitAPIException ex) {
            log.error(ex.getMessage());
        }
    }

    public SshdSessionFactory getSshdSessionFactory(String vcsType, String accessToken, String jobId) {
        File sshDir = generateWorkspaceSshFolder(vcsType, accessToken, jobId);
        SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
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
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir)
                .build(new JGitKeyCache());

        return sshdSessionFactory;
    }

    private File generateWorkspaceSshFolder(String vcsType, String privateKey, String jobId) {
        String sshId = UUID.randomUUID().toString();
        String sshFileName = vcsType.split("~")[1];
        String sshFilePath = String.format(SSH_DIRECTORY, FileUtils.getUserDirectoryPath(), jobId, sshId, sshFileName);
        File sshFile = new File(sshFilePath);
        try {
            log.info("Creating new SSH folder for job {} sshId {}", jobId, sshId);
            FileUtils.forceMkdirParent(sshFile);
            FileUtils.writeStringToFile(sshFile, privateKey, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sshFile.getParentFile();
    }

    private CredentialsProvider setupCredentials(String vcsType, String accessToken) {
        CredentialsProvider credentialsProvider = null;
        log.info("vcsType: {}", vcsType);
        switch (vcsType) {
            case "GITHUB":
                credentialsProvider = new UsernamePasswordCredentialsProvider(accessToken, "");
                break;
            case "BITBUCKET":
                credentialsProvider = new UsernamePasswordCredentialsProvider("x-token-auth", accessToken);
                break;
            case "GITLAB":
                credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", accessToken);
                break;
            case "AZURE_DEVOPS":
                credentialsProvider = new UsernamePasswordCredentialsProvider("dummy", accessToken);
                break;
            default:
                credentialsProvider = null;
                break;
        }
        return credentialsProvider;
    }

}
