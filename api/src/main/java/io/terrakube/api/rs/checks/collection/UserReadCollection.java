package io.terrakube.api.rs.checks.collection;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.terrakube.api.plugin.security.user.AuthenticatedUser;
import io.terrakube.api.rs.collection.item.Item;

import java.util.Optional;

@Slf4j
@SecurityCheck(UserReadCollection.RULE)
public class UserReadCollection extends OperationCheck<Item> {

    public static final String RULE = "user read collection";

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Item item, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.debug("user view collection {}", item.getId());
        return !item.isSensitive();
    }


}
