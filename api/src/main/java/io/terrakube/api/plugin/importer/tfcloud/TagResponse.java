package io.terrakube.api.plugin.importer.tfcloud;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TagResponse {
    private List<TagData> data;

    @Getter
    @Setter
    public static class TagData {
        private String id;
        private String type;
        private TagAttributes attributes;

        @Getter
        @Setter
        public static class TagAttributes {
            private String name;
            private String createdAt;
            private int instanceCount;
        }
    }
}