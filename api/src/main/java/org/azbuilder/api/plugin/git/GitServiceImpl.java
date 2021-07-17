package org.azbuilder.api.plugin.git;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    private static final String GIT_REPOSITORIES = "/.terraform-spring-boot/repositories/";

    @Override
    public File getCloneRepositoryByTag(String repository, String tag) {
        File gitCloneRepository = null;
        try {
            String userHomeDirectory = FileUtils.getUserDirectoryPath();
            String executorPath = userHomeDirectory.concat(
                    FilenameUtils.separatorsToSystem(
                            GIT_REPOSITORIES + "/" + UUID.randomUUID()
                    ));
            gitCloneRepository = new File(executorPath);
            FileUtils.forceMkdir(gitCloneRepository);
            FileUtils.cleanDirectory(gitCloneRepository);
            Git git = Git.cloneRepository()
                    .setURI(repository)
                    .setDirectory(gitCloneRepository)
                    .setBranch("refs/tags/" + tag)
                    .call();
        } catch (GitAPIException | IOException ex) {
            log.error(ex.getMessage());
        }
        return gitCloneRepository;
    }
}
