package org.azbuilder.api.plugin.security.groups.local;

import org.azbuilder.api.plugin.security.groups.GroupService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "org.azbuilder.api.groups", name = "type", havingValue = "LOCAL")
public class LocalGroupServiceImpl implements GroupService {
    @Override
    public boolean isMember(String user, String group) {
        return true;
    }
}
