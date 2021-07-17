package org.azbuilder.api.plugin.git;

import java.io.File;

public interface GitService {

    File getCloneRepositoryByTag(String repository, String tag);
}
