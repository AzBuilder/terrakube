package org.azbuilder.api.rs.hook.module;


import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.registry.configuration.RegistryStorageProperties;
import org.azbuilder.api.rs.module.Module;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
public class CreateHook implements LifeCycleHook<Module> {

    @Autowired
    RegistryStorageProperties registryStorageProperties;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Module module, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("ModuleCreateHook");
    }
}
