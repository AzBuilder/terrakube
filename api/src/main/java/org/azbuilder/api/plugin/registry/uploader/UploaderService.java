package org.azbuilder.api.plugin.registry.uploader;

import org.azbuilder.api.rs.module.Definition;

public interface UploaderService {

    String saveDefinition(Definition definition);
}
