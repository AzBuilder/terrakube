package org.terrakube.api.plugin.security.groups.dex;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.yahoo.elide.core.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.security.groups.GroupService;

import java.util.*;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "org.terrakube.api.groups", name = "type", havingValue = "DEX")
public class DexGroupServiceImpl implements GroupService {
    @Override
    public boolean isMember(User user, String group) {
        JwtAuthenticationToken principal = ((JwtAuthenticationToken) user.getPrincipal());
        boolean isMember = false;
        for (String groupName : toStringArray((JSONArray) principal.getTokenAttributes().get("groups"))) {
            if (groupName.equals(group))
                isMember = true;
        }
        log.info("{} is member {} {}", principal.getTokenAttributes().get("name"), group, isMember);
        return isMember;
    }

    @Override
    public boolean isServiceMember(User user, String group) {
        JwtAuthenticationToken principal = ((JwtAuthenticationToken) user.getPrincipal());
        boolean isMember = principal.getTokenAttributes().get("iss").equals("TerrakubeInternal")? true: false;
        for (String groupName : toStringArray((JSONArray) principal.getTokenAttributes().get("groups"))) {
            if (groupName.equals(group))
                isMember = true;
        }
        log.info("{} is member {} {}", principal.getTokenAttributes().get("name"), group, isMember);
        return isMember;
    }

    private String[] toStringArray(JSONArray array) {
        if (array == null)
            return new String[0];

        String[] arr = new String[array.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (String) array.get(i);
        }
        return arr;
    }
}
