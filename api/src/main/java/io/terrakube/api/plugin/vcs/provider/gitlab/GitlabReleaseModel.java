
package io.terrakube.api.plugin.vcs.provider.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabReleaseModel {
    @JsonProperty("object_kind")
    private String objectKind;

    @JsonProperty("object_attributes")
    private ObjectAttributes objectAttributes;

    private Project project;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectAttributes {
        private String id;
        private String name;
        private String description;
        private String tag;
        private String action;
        private String url;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("released_at")
        private String releasedAt;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Project {
        private String id;
        private String name;
        private String description;

        @JsonProperty("web_url")
        private String webUrl;

        @JsonProperty("git_ssh_url")
        private String gitSshUrl;

        @JsonProperty("git_http_url")
        private String gitHttpUrl;

        @JsonProperty("namespace")
        private String namespace;

        @JsonProperty("visibility_level")
        private int visibilityLevel;

        @JsonProperty("path_with_namespace")
        private String pathWithNamespace;

        @JsonProperty("default_branch")
        private String defaultBranch;

        private String homepage;
        private String url;

        @JsonProperty("ssh_url")
        private String sshUrl;

        @JsonProperty("http_url")
        private String httpUrl;
    }
}