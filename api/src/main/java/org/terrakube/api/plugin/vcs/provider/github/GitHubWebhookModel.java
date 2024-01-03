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
}

@Getter
@Setter
class Commit{
    List<String> added;
    List<String> modified;
    List<String> removed;
}
