package org.terrakube.api.plugin.importer.tfcloud;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VariableResponse {
    private List<VariableData> data;

    @Getter
    @Setter
    public static class VariableData {
        private String id;
        private String type;
        private VariableAttributes attributes;

        @Getter
        @Setter
        public static class VariableAttributes {
            private String key;
            private String value;
            private String description;
            private boolean sensitive;
            private String category;
            private boolean hcl;
        }
    }
}
