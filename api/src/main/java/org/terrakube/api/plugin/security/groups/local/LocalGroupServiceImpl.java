package org.terrakube.api.plugin.security.groups.local;

import com.yahoo.elide.core.security.User;
import org.terrakube.api.plugin.security.groups.GroupService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "org.terrakube.api.groups", name = "type", havingValue = "LOCAL")
public class LocalGroupServiceImpl implements GroupService {
    @Override
    public boolean isMember(User user, String group) {
        return true;
    }

    @Override
    public boolean isServiceMember(String application, String group) {
        return true;
    }
}
