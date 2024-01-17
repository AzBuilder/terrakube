package org.terrakube.api.plugin.vcs.provider.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabWebhookModel {
    @JsonProperty("checkout_sha")
    String checkoutSha;
    List<Commit> commits;
    String ref;
    String before;
    String after;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Commit{
    List<String> added;
    List<String> modified;
    List<String> removed;
}