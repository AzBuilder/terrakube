package org.azbuilder.api.rs.hook.definition;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.registry.uploader.UploaderService;
import org.azbuilder.api.rs.module.Definition;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class CreateHook implements LifeCycleHook<Definition> {

    private UploaderService uploaderService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Definition definition, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("DefinitionCreateHook: {}", definition.getVersion());
        uploaderService.saveDefinition(definition);
    }
}
