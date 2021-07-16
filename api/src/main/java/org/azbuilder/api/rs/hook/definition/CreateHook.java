package org.azbuilder.api.rs.hook.definition;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import org.azbuilder.api.rs.module.Definition;

import java.util.Optional;

public class CreateBinding implements LifeCycleHook<Definition> {
    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Definition definition, RequestScope requestScope, Optional<ChangeSpec> optional) {
        
    }
}
