package org.azbuilder.registry.service.git;

import java.io.File;

public interface GitService {

    File getCloneRepositoryByTag(String repository, String tag, String vcsType, String accessToken);
}
