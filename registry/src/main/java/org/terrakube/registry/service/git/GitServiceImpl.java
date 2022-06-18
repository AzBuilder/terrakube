package org.terrakube.registry.service.git;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    private static final String GIT_DIRECTORY = "/.terraform-spring-boot/git/";

    @Override
    public File getCloneRepositoryByTag(String repository, String tag, String vcsType, String accessToken) {
        File gitCloneRepository = null;
        try {
            String userHomeDirectory = FileUtils.getUserDirectoryPath();
            String gitRepositoryPath = userHomeDirectory.concat(
                    FilenameUtils.separatorsToSystem(
                            GIT_DIRECTORY + "/" + UUID.randomUUID()
                    ));
            gitCloneRepository = new File(gitRepositoryPath);
            FileUtils.forceMkdir(gitCloneRepository);
            FileUtils.cleanDirectory(gitCloneRepository);

            String correctTag = validateCorrectTag(tag, repository, setupCredentials(vcsType, accessToken));

            Git.cloneRepository()
                    .setURI(repository)
                    .setDirectory(gitCloneRepository)
                    .setBranch("refs/tags/" + correctTag)
                    .setCredentialsProvider(setupCredentials(vcsType, accessToken))
                    .call();
        } catch (GitAPIException | IOException ex) {
            log.error(ex.getMessage());
        }
        return gitCloneRepository;
    }

    private CredentialsProvider setupCredentials(String vcsType, String accessToken) {
        CredentialsProvider credentialsProvider = null;
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

    private String validateCorrectTag(String originalTag, String repository, CredentialsProvider credentialsProvider) {
        List<String> versionList = new ArrayList<>();
        String finalTag = originalTag;
        Map<String, Ref> tags = null;
        try {
            tags = Git.lsRemoteRepository()
                    .setTags(true)
                    .setRemote(repository)
                    .setCredentialsProvider(credentialsProvider)
                    .callAsMap();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        tags.forEach((key, value) -> {
            versionList.add(key.replace("refs/tags/", ""));
        });

        if (versionList.contains("v" + originalTag))
            finalTag = "v" + originalTag;

        return finalTag;
    }
}
