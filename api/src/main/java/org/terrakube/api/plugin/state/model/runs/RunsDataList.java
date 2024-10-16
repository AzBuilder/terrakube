package org.terrakube.api.plugin.state.model.runs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RunsDataList {
    List<RunsModel> data;
    @JsonProperty("current-page")
    int currentPage;
    @JsonProperty("total-pages")
    int totalPages;
}
