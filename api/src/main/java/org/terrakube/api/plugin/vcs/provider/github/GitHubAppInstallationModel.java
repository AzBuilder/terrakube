package org.terrakube.api.plugin.vcs.provider.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubAppInstallationModel {
    String id;
    Account account;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Account {
    String login;
}