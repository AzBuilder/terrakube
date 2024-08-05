package org.terrakube.api.plugin.vcs.provider.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubWebhookModel {
    List<Commit> commits;
    HeadCommit head_commit;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Commit{
    List<String> added;
    List<String> modified;
    List<String> removed;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class HeadCommit{
    String id;
}