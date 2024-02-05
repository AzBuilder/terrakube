package org.terrakube.api.plugin.importer.tfcloud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateVersion {
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String id;
        private String type;
        @JsonProperty("attributes")
        private Attributes attributes;
    }

    @Getter
    @Setter
    public static class Attributes {
        @JsonProperty("hosted-state-download-url")
        private String hostedStateDownloadUrl;
        @JsonProperty("hosted-json-state-download-url")
        private String hostedJsonStateDownloadUrl;
    }
}
