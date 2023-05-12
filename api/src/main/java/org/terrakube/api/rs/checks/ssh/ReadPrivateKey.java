package org.terrakube.api.rs.checks.ssh;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.terrakube.api.plugin.security.user.AuthenticatedUser;
import org.terrakube.api.rs.ssh.Ssh;

import java.util.Optional;

@SecurityCheck(ReadPrivateKey.RULE)
public class ReadPrivateKey extends OperationCheck<Ssh> {
    public static final String RULE = "read private key";
    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public boolean ok(Ssh ssh, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(authenticatedUser.isServiceAccount(requestScope.getUser())){
            return true;
        }
        return false;
    }
}
