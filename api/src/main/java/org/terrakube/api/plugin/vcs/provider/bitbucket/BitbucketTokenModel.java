package org.terrakube.api.plugin.vcs.provider.bitbucket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitbucketTokenModel {
    private Push push;
}


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Diff{
    private String href;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Links{
    private Diff diff;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Change{
    private Links links;
    @JsonProperty("new")
    private NewCommit newCommit;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Push{
    private List<Change> changes;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Target {
    private String hash;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class NewCommit {
    private Target target;
}