package org.terrakube.api.plugin.importer.tfcloud;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WorkspaceListResponse {
    private List<WorkspaceImport.WorkspaceData> data;
}