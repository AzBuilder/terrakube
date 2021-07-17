package org.azbuilder.api.plugin.registry;

import org.azbuilder.api.rs.module.Definition;

public interface RegistryStorage {

    String modulePath(Definition definition);
}
