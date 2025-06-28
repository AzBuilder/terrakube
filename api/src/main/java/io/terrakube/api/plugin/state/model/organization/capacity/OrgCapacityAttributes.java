package io.terrakube.api.plugin.state.model.organization.capacity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrgCapacityAttributes {

    int pending;
    int running;
}
