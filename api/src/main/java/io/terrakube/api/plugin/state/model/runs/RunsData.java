package io.terrakube.api.plugin.state.model.runs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RunsData {
    RunsModel data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List included;
}
