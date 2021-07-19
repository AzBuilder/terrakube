package org.azbuilder.registry.service.git;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class GitServiceImpl implements GitService{

    private static final String GIT_DIRECTORY="/.terraform-spring-boot/git/";

    @Override
    public File getCloneRepositoryByTag(String repository, String tag) {
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
