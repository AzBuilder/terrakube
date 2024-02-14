package org.terrakube.api.plugin.importer.tfcloud;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class WorkspaceListResponse {
    private List<WorkspaceImport.WorkspaceData> data;
    private WorkspaceMeta meta;

    @Getter
    @Setter
    public static class WorkspaceMeta {
        private Pagination pagination;

        @Getter
        @Setter
        public static class Pagination {
            @JsonProperty("current-page")
            private int currentPage;

            @JsonProperty("prev-page")
            private Integer prevPage;

            @JsonProperty("next-page")
            private Integer nextPage;

            @JsonProperty("total-pages")
            private int totalPages;

            @JsonProperty("total-count")
            private int totalCount;
        }
    }
}