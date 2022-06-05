package org.azbuilder.registry.service.module;

import java.util.List;

public interface ModuleService {

    List<String> getAvailableVersions(String organizationName, String moduleName, String providerName);

    String getModuleVersionPath(String organizationName, String moduleName, String providerName, String version, boolean countDownload);
}
