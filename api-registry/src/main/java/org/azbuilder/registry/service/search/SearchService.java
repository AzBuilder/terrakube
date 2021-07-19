package org.azbuilder.registry.service.search;

import java.util.List;

public interface SearchService {

    List<String> getAvailableVersions(String organizationName, String moduleName, String providerName);

    String getModuleVersionPath(String organizationName, String moduleName, String providerName, String version);
}
