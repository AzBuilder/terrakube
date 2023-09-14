package org.terrakube.api.plugin.tokens.team;

import com.nimbusds.jose.shaded.json.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TeamTokenService {

    public List<String> getCurrentGroups(JwtAuthenticationToken principalJwt ){
        Object groups = principalJwt.getTokenAttributes().get("groups");

        JSONArray array = (JSONArray) groups;
        List<String> list = new ArrayList();
        for(int i = 0; i < array.size(); i++){
            list.add(array.get(i).toString());
        }
        log.info(list.toString());

        return list;
    }
}
