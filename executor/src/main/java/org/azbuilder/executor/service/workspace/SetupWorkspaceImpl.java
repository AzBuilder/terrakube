package org.azbuilder.executor.service.workspace;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FilenameUtils;
import org.azbuilder.executor.service.mode.TerraformJob;
import org.azbuilder.executor.service.workspace.registry.SetupPrivateRegistry;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.*;

@Slf4j
@Service
public class SetupWorkspaceImpl implements SetupWorkspace {

    private static final String EXECUTOR_DIRECTORY = "/.terraform-spring-boot/executor/";

    OkHttpClient httpClient;
    SetupPrivateRegistry setupPrivateRegistry;
    boolean enableRegistrySecurity;

    public SetupWorkspaceImpl(OkHttpClient httpClient, SetupPrivateRegistry setupPrivateRegistry, @Value("${org.azbuilder.api.enableSecurity}") boolean enableRegistrySecurity){
        this.httpClient = httpClient;
        this.setupPrivateRegistry = setupPrivateRegistry;
        this.enableRegistrySecurity = enableRegistrySecurity;
    }

    @Override
    public File prepareWorkspace(TerraformJob terraformJob) {
        File workspaceFolder = null;
        try {
            workspaceFolder = setupWorkspaceDirectory(terraformJob.getOrganizationId(), terraformJob.getWorkspaceId());
            downloadWorkspace(workspaceFolder, terraformJob.getSource(), terraformJob.getBranch(), terraformJob.getVcsType(), terraformJob.getAccessToken());
            if (enableRegistrySecurity)
                setupPrivateRegistry.addCredentials(workspaceFolder);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return workspaceFolder;
        }
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

    private void downloadWorkspace(File workspaceFolder, String source, String branch, String vcsType, String accessToken) throws IOException {
        try {
            Git.cloneRepository()
                    .setURI(source)
                    .setDirectory(workspaceFolder)
                    .setCredentialsProvider(setupCredentials(vcsType, accessToken))
                    .setBranch(branch)
                    .call();
        } catch (GitAPIException ex) {
            log.error(ex.getMessage());
        }
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
