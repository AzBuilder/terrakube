package org.terrakube.executor.service.workspace;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.ServerKeyDatabase;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;
import org.terrakube.executor.service.mode.TerraformJob;
import org.terrakube.executor.service.workspace.security.WorkspaceSecurity;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SetupWorkspaceImpl implements SetupWorkspace {

    private static final String EXECUTOR_DIRECTORY = "%s/.terraform-spring-boot/executor/%s/%s/.originRepository";
    private static final String SSH_DIRECTORY = "%s/.terraform-spring-boot/ssh/executor/%s/%s/%s";
    private static final String SSH_EXECUTOR_JOB = "%s/.terraform-spring-boot/ssh/executor/%s";

    WorkspaceSecurity workspaceSecurity;
    boolean enableRegistrySecurity;

    public SetupWorkspaceImpl(WorkspaceSecurity workspaceSecurity, @Value("${org.terrakube.client.enableSecurity}") boolean enableRegistrySecurity) {
        this.workspaceSecurity = workspaceSecurity;
        this.enableRegistrySecurity = enableRegistrySecurity;
    }

    @Override
    public File prepareWorkspace(TerraformJob terraformJob) {
        File workspaceCloneFolder = null;
        try {
            workspaceCloneFolder = setupWorkspaceDirectory(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
            if (!terraformJob.getBranch().equals("remote-content")) {
                downloadWorkspace(workspaceCloneFolder, terraformJob.getSource(), terraformJob.getBranch(), terraformJob.getFolder(), terraformJob.getVcsType(), terraformJob.getAccessToken(), terraformJob.getJobId());
            } else {
                downloadWorkspaceTarGz(workspaceCloneFolder.getParentFile(), terraformJob.getSource());
            }
            if (enableRegistrySecurity)
                workspaceSecurity.addTerraformCredentials(workspaceCloneFolder);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return workspaceCloneFolder != null ? workspaceCloneFolder.getParentFile() : new File("/tmp/" + UUID.randomUUID());
    }

    private File setupWorkspaceDirectory(String organizationId, String workspaceId) throws IOException {
        String userHomeDirectory = FileUtils.getUserDirectoryPath();
        log.info("User Home Directory: {}", userHomeDirectory);

        String executorPath = String.format(EXECUTOR_DIRECTORY, userHomeDirectory, organizationId, workspaceId);
        File executorFolder = new File(executorPath);
        FileUtils.forceMkdir(executorFolder);
        FileUtils.cleanDirectory(executorFolder);
        log.info("Workspace git clone directory: {}", executorFolder.getPath());
        log.info("Workspace working directory: {}", executorFolder.getParentFile().getPath());
        return executorFolder;
    }

    private void downloadWorkspace(File gitCloneFolder, String source, String branch, String workspaceFolder, String vcsType, String accessToken, String jobId) throws IOException {
        try {
            if (vcsType.startsWith("SSH")) {
                Git.cloneRepository()
                        .setURI(source)
                        .setDirectory(gitCloneFolder)
                        .setBranch(branch)
                        .setTransportConfigCallback(transport -> {
                            ((SshTransport) transport).setSshSessionFactory(getSshdSessionFactory(vcsType, accessToken, jobId));
                        })
                        .call();
                FileUtils.deleteDirectory(new File(String.format(SSH_EXECUTOR_JOB, FileUtils.getUserDirectoryPath(), jobId)));
            } else {
                Git.cloneRepository()
                        .setURI(source)
                        .setDirectory(gitCloneFolder)
                        .setCredentialsProvider(setupCredentials(vcsType, accessToken))
                        .setBranch(branch)
                        .call();
            }

            getCommitId(gitCloneFolder);

            log.info("Copy files from folder {} to {}", gitCloneFolder.getPath() + workspaceFolder, gitCloneFolder.getParentFile().getPath());
            File finalWorkspaceFolder = new File(gitCloneFolder.getPath() + workspaceFolder);
            if (finalWorkspaceFolder.exists())
                for (File srcFile : finalWorkspaceFolder.listFiles()) {
                    log.info("Copy {} to {}", srcFile.getName(), gitCloneFolder.getParentFile().getPath());
                    if (srcFile.isDirectory()) {
                        FileUtils.copyDirectoryToDirectory(srcFile, gitCloneFolder.getParentFile());
                    } else {
                        FileUtils.copyFileToDirectory(srcFile, gitCloneFolder.getParentFile());
                    }
                }
            else {
                log.error("Folder {} does not exists in the repository", workspaceFolder);
            }

        } catch (GitAPIException ex) {
            log.error(ex.getMessage());
        }
    }

    private void downloadWorkspaceTarGz(File tarGzFolder, String source) throws IOException {
        File terraformTarGz = new File(tarGzFolder.getPath() + "/terraformContent.tar.gz");
        FileUtils.copyURLToFile(new URL(source), terraformTarGz, 10000, 10000);
        extractTarGZ(new FileInputStream(terraformTarGz), tarGzFolder.getPath().toString());
    }

    public void extractTarGZ(InputStream in, String destinationFilePath) throws IOException {
        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(in);
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File f = new File(destinationFilePath + "/" + entry.getName());
                    boolean created = f.mkdir();
                    if (!created) {
                        log.info("Unable to create directory '{}', during extraction of archive contents.\n", f.getAbsolutePath());
                    }
                } else {
                    int count;
                    byte data[] = new byte[2048];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath + "/" + entry.getName(), false);
                    log.info("Adding file {} to workspace context", destinationFilePath + "/" + entry.getName());
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, 2048)) {
                        while ((count = tarIn.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, count);
                        }
                    }
                }
            }

            log.info("Untar completed successfully!");
        }
    }

    private void getCommitId(File gitCloneFolder) {
        RevCommit latestCommit = null;
        try {
            latestCommit = Git.init().setDirectory(gitCloneFolder).call().
                    log().
                    setMaxCount(1).
                    call().
                    iterator().
                    next();
            String latestCommitHash = latestCommit.getName();
            log.info("Commit Id: {}", latestCommitHash);
            String commitInfoFile = String.format("%s/commitHash.info", gitCloneFolder.getParentFile().getPath());
            log.info("Writing commit id to {}", commitInfoFile);
            FileUtils.writeStringToFile(new File(commitInfoFile), latestCommitHash, Charset.defaultCharset());

        } catch (GitAPIException | IOException e) {
            log.error(e.getMessage());
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
            log.error(e.getMessage());
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
