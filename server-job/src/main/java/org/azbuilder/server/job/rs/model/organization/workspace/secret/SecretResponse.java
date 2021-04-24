package org.azbuilder.server.job.rs.model.organization.workspace.secret;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SecretResponse {
    List<Secret> data;
}
